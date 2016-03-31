package geotrellis.test

import geotrellis.core.LayoutSchemeArg
import geotrellis.core.poly.{PolyWrite, PolyValidate, PolyCombine, PolyIngest}
import geotrellis.raster._
import geotrellis.spark.io._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.index.KeyIndexMethod
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.spark._
import geotrellis.util.{S3Support, HadoopSupport, SparkSupport}
import geotrellis.vector.{ProjectedExtent, Extent}
import geotrellis.config._

import com.typesafe.config.{Config => TConfig}
import org.apache.spark.rdd.RDD
import org.joda.time.DateTime
import spray.json.JsonFormat
import shapeless._
import shapeless.poly._

import scala.reflect.ClassTag

abstract class TestEnvironment[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](val dataSet: DataSet) extends SparkSupport with HadoopSupport with Serializable { // HadoopSupport mixin is deprecated
  type M = TileLayerMetadata[K]

  // Poly functions cse types
  type Ingest      = String :: KeyIndexMethod[K] :: LayoutSchemeArg :: RDD[(I, V)] :: LayerWriter[LayerId] :: HNil
  type Validate    = TileLayerMetadata[K] :: String  :: LayerId :: Option[DateTime] :: ((LayerId, Option[Extent]) => RDD[(K, V)] with Metadata[M]) :: HNil
  type ValidateAux = (Option[Raster[V]], Option[Raster[V]], List[Raster[V]])
  type Combine     = LayerId :: RDD[(K, V)] with Metadata[M] :: HNil
  type OWrite      = Option[Raster[V]] :: String :: HNil
  type LWrite      = List[Raster[V]] :: String :: HNil

  val layerName: String
  val zoom: Int

  val writer: LayerWriter[LayerId]
  val reader: FilteringLayerReader[LayerId]
  val attributeStore: AttributeStore

  def loadTiles: RDD[(I, V)]

  def validate(input: RDD[(I, V)], ingested: RDD[(K, V)] with Metadata[M]): Int = {
    val threshold = 0.01 // This could be different for each dataset

    val md = ingested.metadata
    val fullExtent = md.extent
    val validationExtent = someRandomExtentWithin(fullExtent) // how to choose one that's not small/big?
    val ingestedTiles = ingested.asRasters.filter(_._2.extent.intersects(validationExtent)).collect
    val inputTiles =
      input
        .map { case (key, value) => (key.extent.reproject(key.crs, md.crs), (key, value)) }
        .filter(_._1.intersects(validationExtent))
        .collect

    for((_, ingestedRaster) <- ingestedTiles) {
      for((_, (ProjectedExtent(extent, crs), tile)) <- inputTiles.filter { case (reprojectedExtent, (projectedExtent, tile)) => reprojectedExtent.intersects(ingestedRaster.extent) }) {
        val transform = Transform(md.crs, crs)
        val inputRasterExtent = RasterExtent(extent, tile.cols, tile.rows)
        cfor(0)(_ < ingestedRaster.tile.cols, _ + 1) { col =>
          cfor(0)(_ < ingestedRaster.tile.rows, _ + 1) { row =>
            val (x, y) = ingestedRaster.rasterExtent.gridToMap(col, row)
            val (rx, ry) = transform(x, y)
            val (icol, irow) = inputRasterExtent.mapToGrid(rx, ry)

            val v1 = ingestedRaster.getDouble(col, row)
            val v2 = tile.getDouble(icol, irow)

            assert(v1 - v2 < threshold, "Oops")
          }
        }
      }

    }

    ingested.asRasters.filter(_._2.extent.intersects)
  }

  val loadParams: Map[String, String]   = dataSet.getLoadParams
  val ingestParams: Map[String, String] = dataSet.getIngestParams

  def read(layerId: LayerId, extent: Option[Extent] = None): RDD[(K, V)] with Metadata[M] = {
    logger.info(s"reading ${layerId}...")
    extent.fold(reader.read[K, V, M](layerId))(e => reader.read[K, V, M](layerId,  new LayerQuery[K, M].where(Intersects(e))))
  }

  def ingest(layer: String, keyIndexMethod: KeyIndexMethod[K], lsa: LayoutSchemeArg)
            (implicit pi: Case[PolyIngest.type, Ingest]): Unit = {
    conf.set("io.map.index.interval", "1")
    logger.info(s"ingesting tiles into accumulo (${layer})...")
    PolyIngest(layer, keyIndexMethod, lsa, loadTiles, writer)
  }

  def combine(layerId: LayerId)(implicit pc: Case[PolyCombine.type, Combine]) = {
    logger.info(s"combineLayer ${layerId}...")
    val rdd = read(layerId)
    PolyCombine(layerId, rdd)
  }

  def validate(layerId: LayerId, dt: Option[DateTime])
              (implicit pv: Case.Aux[PolyValidate.type, Validate, ValidateAux],
                        rw: Case[PolyWrite.type, OWrite],
                        lw: Case[PolyWrite.type, LWrite]): Unit = {
    val metadata = attributeStore.readMetadata[TileLayerMetadata[K]](layerId)
    val (ingestedRaster, expectedRasterResampled, diffRasters) =
      PolyValidate(metadata, mvValidationTiffLocal, layerId, dt, read _)

    PolyWrite(ingestedRaster, s"${validationDir}ingested.${this.getClass.getName}")
    PolyWrite(expectedRasterResampled, s"${validationDir}expected.${this.getClass.getName}")
    PolyWrite(diffRasters, s"${validationDir}diff.${this.getClass.getName}")
  }

  def ingest(implicit pi: Case[PolyIngest.type, Ingest]): Unit =
    ingest(layerName, dataSet.typedKeyIndexMethod[K], dataSet)

  def combine(implicit pc: Case[PolyCombine.type, Combine]): Unit = combine(LayerId(layerName, zoom))

  def validate(dt: Option[DateTime])
              (implicit pv: Case.Aux[PolyValidate.type, Validate, ValidateAux],
                        rw: Case[PolyWrite.type, OWrite],
                        lw: Case[PolyWrite.type, LWrite]): Unit =
    validate(LayerId(layerName, zoom), dt)
  
  def validate(implicit pv: Case.Aux[PolyValidate.type, Validate, ValidateAux],
                          rw: Case[PolyWrite.type, OWrite],
                          lw: Case[PolyWrite.type, LWrite]): Unit = validate(None)
}
