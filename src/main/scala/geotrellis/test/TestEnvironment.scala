package geotrellis.test

import geotrellis.core.LayoutSchemeArg
import geotrellis.core.poly.{PolyCombine, PolyIngest, PolyValidate, PolyWrite}
import geotrellis.raster._
import geotrellis.spark.io._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.index.KeyIndexMethod
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.spark._
import geotrellis.util.{Component, HadoopSupport, SparkSupport}
import geotrellis.vector.{Extent, ProjectedExtent}
import geotrellis.config._
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
  type Validate    = DataSet :: TileLayerMetadata[K] :: String  :: LayerId :: Option[DateTime] :: ((LayerId, Option[Extent]) => RDD[(K, V)] with Metadata[M]) :: HNil
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
    val (ingestedRasterSubset, expectedRaster, rasterDiff) =
      PolyValidate(dataSet, metadata, mvValidationTiffLocal, layerId, dt, read _)

    PolyWrite(ingestedRasterSubset, s"${validationDir}ingested.${this.getClass.getName}")
    PolyWrite(expectedRaster, s"${validationDir}expected.${this.getClass.getName}")
    PolyWrite(rasterDiff, s"${validationDir}diff.${this.getClass.getName}")
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
