package geotrellis.test

import geotrellis.config.json.backend.JCredensials
import geotrellis.config.json.dataset.{JConfig, JIngestOptions}
import geotrellis.core.poly.{PolyCombine, PolyIngest, PolyValidate, PolyWrite}
import geotrellis.raster._
import geotrellis.spark.io._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.index.KeyIndexMethod
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.spark._
import geotrellis.util.{Component, SparkSupport}
import geotrellis.vector.{Extent, ProjectedExtent}
import org.apache.spark.rdd.RDD
import org.joda.time.DateTime
import spray.json.JsonFormat
import shapeless.poly._

import scala.reflect.ClassTag

abstract class TestEnvironment[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](val jConfig: JConfig, val jCredensials: JCredensials) extends SparkSupport with Serializable {
  type M = TileLayerMetadata[K]

  val writer: LayerWriter[LayerId]
  val reader: FilteringLayerReader[LayerId]
  val attributeStore: AttributeStore

  def loadTiles: RDD[(I, V)]

  val layerName         = jConfig.name
  val loadParams        = jConfig.getLoadParams
  val ingestParams      = jConfig.getIngestParams
  val loadCredensials   = jCredensials.getLoad(jConfig)
  val ingestCredensials = jCredensials.getIngest(jConfig)
  val layerId           = attributeStore.layerIds.filter(_.name == layerName).sortWith(_.zoom > _.zoom).last

  def read(layerId: LayerId, extent: Option[Extent] = None): RDD[(K, V)] with Metadata[M] = {
    logger.info(s"reading ${layerId}...")
    extent.fold(reader.read[K, V, M](layerId))(e => reader.read[K, V, M](layerId,  new LayerQuery[K, M].where(Intersects(e))))
  }

  def ingest(layer: String, keyIndexMethod: KeyIndexMethod[K], jio: JIngestOptions)
            (implicit pi: Case[PolyIngest.type, PolyIngest.In[K, I, V]]): Unit = {
    conf.set("io.map.index.interval", "1")
    logger.info(s"ingesting tiles into accumulo (${layer})...")
    PolyIngest(layer, keyIndexMethod, jio, loadTiles, writer)
  }

  def combine(layerId: LayerId)(implicit pc: Case[PolyCombine.type, PolyCombine.In[K, V, M]]) = {
    logger.info(s"combineLayer ${layerId}...")
    val rdd = read(layerId)
    PolyCombine(layerId, rdd)
  }

  def validate(layerId: LayerId, dt: Option[DateTime])
              (implicit pv: Case.Aux[PolyValidate.type, PolyValidate.In[K, V, M], PolyValidate.Out[V]],
                        rw: Case[PolyWrite.type, PolyWrite.In[Option, V]],
                        lw: Case[PolyWrite.type, PolyWrite.In[List, V]]): Unit = {
    val metadata = attributeStore.readMetadata[TileLayerMetadata[K]](layerId)
    val (ingestedRaster, expectedRasterResampled, diffRasters) =
      PolyValidate(metadata, jConfig.validationOptions.tiffLocal, layerId, dt, read _)

    PolyWrite(ingestedRaster, s"${jConfig.validationOptions.tmpDir}ingested.${this.getClass.getName}")
    PolyWrite(expectedRasterResampled, s"${jConfig.validationOptions.tmpDir}expected.${this.getClass.getName}")
    PolyWrite(diffRasters, s"${jConfig.validationOptions.tmpDir}diff.${this.getClass.getName}")
  }

  def ingest(implicit pi: Case[PolyIngest.type, PolyIngest.In[K, I, V]]): Unit =
    ingest(layerName, jConfig.ingestOptions.keyIndexMethod.getKeyIndexMethod[K], jConfig.ingestOptions)

  def combine(implicit pc: Case[PolyCombine.type, PolyCombine.In[K, V, M]]): Unit = combine(layerId)

  def validate(dt: Option[DateTime])
              (implicit pv: Case.Aux[PolyValidate.type, PolyValidate.In[K, V, M], PolyValidate.Out[V]],
                        rw: Case[PolyWrite.type, PolyWrite.In[Option, V]],
                        lw: Case[PolyWrite.type, PolyWrite.In[List, V]]): Unit = validate(layerId, dt)

  def validate(implicit pv: Case.Aux[PolyValidate.type, PolyValidate.In[K, V, M], PolyValidate.Out[V]],
                          rw: Case[PolyWrite.type, PolyWrite.In[Option, V]],
                          lw: Case[PolyWrite.type, PolyWrite.In[List, V]]): Unit = validate(jConfig.validationOptions.dateTime)

  def run(implicit pi: Case[PolyIngest.type, PolyIngest.In[K, I, V]],
                   pc: Case[PolyCombine.type, PolyCombine.In[K, V, M]],
                   pv: Case.Aux[PolyValidate.type, PolyValidate.In[K, V, M], PolyValidate.Out[V]],
                   rw: Case[PolyWrite.type, PolyWrite.In[Option, V]],
                   lw: Case[PolyWrite.type, PolyWrite.In[List, V]]) = { ingest; combine; validate }
}
