package geotrellis.test

import geotrellis.config.json.backend.JCredentials
import geotrellis.config.json.dataset.{JConfig, JIngestOptions}
import geotrellis.core.poly._
import geotrellis.proj4.Transform
import geotrellis.raster._
import geotrellis.spark.io._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.index.KeyIndexMethod
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.spark._
import geotrellis.test.validation.ValidationUtilities
import geotrellis.util._
import geotrellis.util.Colors._
import geotrellis.vector.{Extent, ProjectedExtent}

import org.apache.spark.rdd.RDD
import org.joda.time.DateTime
import spray.json.JsonFormat
import shapeless.poly._
import spire.syntax.cfor._

import scala.reflect.ClassTag

abstract class TestEnvironment[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](@transient val jConfig: JConfig, val jCredentials: JCredentials)(implicit @transient kf: JsonFormat[K]) extends SparkSupport with ValidationUtilities with LoggingSummary with Serializable {
  type M = TileLayerMetadata[K]

  val writer: LayerWriter[LayerId]
  val reader: FilteringLayerReader[LayerId]
  val copier: LayerCopier[LayerId]
  val mover: LayerMover[LayerId]
  val reindexer: LayerReindexer[LayerId]
  val deleter: LayerDeleter[LayerId]
  val updater: LayerUpdater[LayerId]
  val attributeStore: AttributeStore

  def loadTiles: RDD[(I, V)]

  val layerName         = jConfig.name
  val loadParams        = jConfig.getLoadParams
  val ingestParams      = jConfig.getIngestParams
  val loadCredentials   = jCredentials.getLoad(jConfig)
  val ingestCredentials = jCredentials.getIngest(jConfig)

  lazy val layerId        = attributeStore.layerIds.filter(_.name == layerName).sortWith(_.zoom > _.zoom).head
  lazy val copyLayerId    = layerId.copy(name = s"${layerName}-copy-${DateTime.now.getMillis}")
  lazy val moveLayerId    = layerId.copy(name = s"${layerName}-move-${DateTime.now.getMillis}")

  def read(append: Boolean = true)(layerId: LayerId, extent: Option[Extent] = None): RDD[(K, V)] with Metadata[M] =
    withSpeedMetrics(s"${jConfig.name}.read", append) {
      logger.info(green(s"reading ${layerId}..."))
      extent.fold(reader.read[K, V, M](layerId))(e => reader.read[K, V, M](layerId, new LayerQuery[K, M].where(Intersects(e))))
    }

  def ingest(layer: String, keyIndexMethod: KeyIndexMethod[K], jio: JIngestOptions)
            (implicit pi: Case[PolyIngest.type, PolyIngest.In[K, I, V]]): Unit =
    withSpeedMetrics(s"${jConfig.name}.ingest") {
      conf.set("io.map.index.interval", "1")
      logger.info(s"ingesting tiles into ${jConfig.`type`.ingestBackend} (${layer})...")
      PolyIngest(layer, keyIndexMethod, jio, loadTiles, writer)
    }

  def combine(layerId: LayerId)(implicit pc: Case[PolyCombine.type, PolyCombine.In[K, V, M]]) =
    withSpeedMetrics(s"${jConfig.name}.combine") {
      logger.info(green(s"combineLayer ${layerId}..."))
      val rdd = read(false)(layerId)
      PolyCombine(layerId, rdd)
    }

  def validate(layerId: LayerId, dt: Option[DateTime])
              (implicit pv: Case.Aux[PolyValidate.type, PolyValidate.In[K, V, M], PolyValidate.Out[V]],
                        rw: Case[PolyWrite.type, PolyWrite.In[Option, V]],
                        lw: Case[PolyWrite.type, PolyWrite.In[List, V]]): Unit =
    withSpeedMetrics(s"${jConfig.name}.validate") {
      val metadata = attributeStore.readMetadata[TileLayerMetadata[K]](layerId)
      val (ingestedRaster, expectedRasterResampled, diffRasters) =
        PolyValidate(metadata, jConfig, layerId, dt, read(false) _)

      PolyWrite(ingestedRaster, s"${jConfig.validationOptions.tmpDir}ingested.${jConfig.name}")
      PolyWrite(expectedRasterResampled, s"${jConfig.validationOptions.tmpDir}expected.${jConfig.name}")
      PolyWrite(diffRasters, s"${jConfig.validationOptions.tmpDir}diff.${jConfig.name}")
    }

  def ingest(implicit pi: Case[PolyIngest.type, PolyIngest.In[K, I, V]]): Unit =
    ingest(layerName, jConfig.ingestOptions.keyIndexMethod.getKeyIndexMethod[K], jConfig.ingestOptions)

  def combine(implicit pc: Case[PolyCombine.type, PolyCombine.In[K, V, M]]): Unit = combine(layerId)

  def newValidate(implicit pa: Case.Aux[PolyAssert.type, PolyAssert.In[V], PolyAssert.Out]): Unit = newValidate(loadTiles, read(false)(layerId))

  def newValidate(input: RDD[(I, V)], ingested: RDD[(K, V)] with Metadata[M])
                 (implicit pa: Case.Aux[PolyAssert.type, PolyAssert.In[V], PolyAssert.Out]): Unit =
    withSpeedMetrics(s"${jConfig.name}.newValidate") {

      val threshold = jConfig.validationOptions.resolutionThreshold
      val md = ingested.metadata
      val validationExtent = randomExtentWithin(md.extent, jConfig.validationOptions.sampleScale)
      val ingestedTiles = ingested.asRasters.filter(_._2.extent.intersects(validationExtent)).collect
      val inputTiles =
        input
          .map { case (key, value) => (key.extent.reproject(key.getComponent[ProjectedExtent].crs, md.crs), (key, value)) }
          .filter(_._1.intersects(validationExtent))
          .collect

      var nansCount = 0
      var deltaSum = 0d
      var goodsCount = 0
      var badsCount = 0
      var cellsCount = 0

      for ((_, ingestedRaster) <- ingestedTiles) {
        for ((_, (ProjectedExtent(extent, crs), tile)) <- inputTiles.filter { case (reprojectedExtent, (projectedExtent, tile)) => reprojectedExtent.intersects(ingestedRaster.extent) }) {
          val transform = Transform(md.crs, crs)
          val inputRasterExtent = RasterExtent(extent, tile.cols, tile.rows)
          cfor(0)(_ < ingestedRaster.tile.cols, _ + 1) { icol =>
            cfor(0)(_ < ingestedRaster.tile.rows, _ + 1) { irow =>
              val (x, y) = ingestedRaster.rasterExtent.gridToMap(icol, irow)
              val (rx, ry) = transform(x, y)
              val (col, row) = inputRasterExtent.mapToGrid(rx, ry)

              if (extent.contains(rx, ry)) {
                val (isNaN, delta, result) = PolyAssert((ingestedRaster.tile, tile), ((icol, irow), (col, row)), threshold)
                logger.debug(yellow(s"(isNaN, delta, result): ${(isNaN, delta, result)}"))
                cellsCount += 1
                deltaSum += delta
                if (isNaN) {
                  nansCount += 1; goodsCount += 1
                }
                else {
                  if (result) goodsCount += 1
                  else badsCount += 1
                }
              }
            }
          }
        }
      }

      val avgDelta = deltaSum / cellsCount
      val success = avgDelta < threshold
      val infoAppender  = appendLog(s"${jConfig.name}.newValidate") _
      val errorAppender = appendLog(s"${jConfig.name}.newValidate", red(_)) _

      infoAppender(s"threshold: ${threshold}")
      infoAppender(s"Cells count: ${cellsCount}")
      infoAppender(s"NaNs count: ${nansCount}")
      infoAppender(s"deltaSum: ${deltaSum}")
      infoAppender(s"Goods count: ${goodsCount}")
      infoAppender(s"Bads count: ${badsCount}")
      infoAppender(s"NaNs per cell: ${nansCount / cellsCount}")
      infoAppender(s"Average delta: ${avgDelta}")
      infoAppender(s"Average delta < threshold: ${success}")
      if (success) infoAppender(s"New validation test success")
      else errorAppender(s"New validation test failed")
    }

  def validate(dt: Option[DateTime])
              (implicit pv: Case.Aux[PolyValidate.type, PolyValidate.In[K, V, M], PolyValidate.Out[V]],
                        rw: Case[PolyWrite.type, PolyWrite.In[Option, V]],
                        lw: Case[PolyWrite.type, PolyWrite.In[List, V]]): Unit = validate(layerId, dt)

  def validate(implicit pv: Case.Aux[PolyValidate.type, PolyValidate.In[K, V, M], PolyValidate.Out[V]],
                          rw: Case[PolyWrite.type, PolyWrite.In[Option, V]],
                          lw: Case[PolyWrite.type, PolyWrite.In[List, V]]): Unit = validate(jConfig.validationOptions.dateTime)

  def copy(id: LayerId, cid: LayerId): Unit =
    withSpeedMetrics(s"${jConfig.name}.copy") {
      val c = read(false)(id).count()
      copier.copy[K, V, M](id, cid)
      val cc = read(false)(cid).count()

      if (c == cc) appendLog(s"${jConfig.name}.copy")("Copy test success")
      else appendLog(s"${jConfig.name}.copy", red(_))(s"Copy test failed ($c != $cc)")
    }

  def copy: Unit = copy(layerId, copyLayerId)

  def move(id: LayerId, mid: LayerId): Unit =
    withSpeedMetrics(s"${jConfig.name}.move") {
      val c = read(false)(id).count()
      mover.move[K, V, M](id, mid)
      val cc = read(false)(mid).count()

      if (c == cc) appendLog(s"${jConfig.name}.move")("Move test success")
      else appendLog(s"${jConfig.name}.move", red(_))(s"Move test failed ($c != $cc)")
    }

  def move: Unit = move(copyLayerId, moveLayerId)

  def reindex(id: LayerId, keyIndexMethod: KeyIndexMethod[K]): Unit =
    withSpeedMetrics(s"${jConfig.name}.reindex") {
      val c = read(false)(id).count()
      reindexer.reindex[K, V, M](id, keyIndexMethod)
      val cc = read(false)(id).count()

      if (c == cc) appendLog(s"${jConfig.name}.reindex")("Reindex test success")
      else appendLog(s"${jConfig.name}.reindex", red(_))(s"Reindex test failed ($c != $cc)")
    }

  def reindex: Unit = reindex(moveLayerId, jConfig.ingestOptions.keyIndexMethod.getKeyIndexMethod[K])

  def update(id: LayerId, rdd: RDD[(K, V)] with Metadata[M]): Unit =
    withSpeedMetrics(s"${jConfig.name}.update") {
      updater.update[K, V, M](id, rdd)
      val urdd = read(false)(id)

      val (c, cc) = rdd.count() -> urdd.count()
      if (c == cc) appendLog(s"${jConfig.name}.update")("Update test success")
      else appendLog(s"${jConfig.name}.update", red(_))(s"Update test failed ($c != $cc)")
    }

  def update: Unit = update(moveLayerId, read(false)(moveLayerId))

  def delete(id: LayerId): Unit =
    withSpeedMetrics(s"${jConfig.name}.delete") {
      deleter.delete(id)
      try reader.read[K, V, M](id) catch {
        case e: LayerNotFoundError => appendLog(s"${jConfig.name}.delete")("Delete test success")
        case _ => appendLog(s"${jConfig.name}.delete", red(_))(s"Delete test failed")
      }
    }

  def delete: Unit = delete(moveLayerId)

  def run(implicit pi: Case[PolyIngest.type, PolyIngest.In[K, I, V]],
                   pc: Case[PolyCombine.type, PolyCombine.In[K, V, M]],
                   pv: Case.Aux[PolyValidate.type, PolyValidate.In[K, V, M], PolyValidate.Out[V]],
                   rw: Case[PolyWrite.type, PolyWrite.In[Option, V]],
                   lw: Case[PolyWrite.type, PolyWrite.In[List, V]],
                   pa: Case.Aux[PolyAssert.type, PolyAssert.In[V], PolyAssert.Out]) =
    withSpeedMetrics(s"${jConfig.name}.run") {
      beforeAll
      ingest
      combine
      //validate
      newValidate
      copy
      move
      reindex
      update
      afterAll
    }

  def beforeAll: Unit = { }
  def afterAll: Unit = { printSummary(filter = Some(jConfig.name)) }
}
