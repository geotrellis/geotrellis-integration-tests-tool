package geotrellis.test

import geotrellis.core.LayoutSchemeArg
import geotrellis.core.functor.{PolyWrite, PolyValidate, PolyCombine, PolyIngest}
import geotrellis.proj4.WebMercator
import geotrellis.raster._
import geotrellis.raster.io.geotiff.GeoTiff
import geotrellis.raster.io.geotiff.writer.GeoTiffWriter
import geotrellis.spark.io._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.index.KeyIndexMethod
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.spark._
import geotrellis.util.{S3Support, HadoopSupport, SparkSupport}
import geotrellis.vector.{ProjectedExtent, Extent}

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
] extends SparkSupport with HadoopSupport with S3Support with Serializable {
  type M = TileLayerMetadata[K]

  val layerName: String
  val zoom: Int

  val writer: LayerWriter[LayerId]
  val reader: FilteringLayerReader[LayerId]
  val attributeStore: AttributeStore

  def loadTiles: RDD[(I, V)]

  def read(layerId: LayerId, extent: Option[Extent] = None): RDD[(K, V)] with Metadata[M] = {
    logger.info(s"reading ${layerId}...")
    extent.fold(reader.read[K, V, M](layerId))(e => reader.read[K, V, M](layerId,  new LayerQuery[K, M].where(Intersects(e))))
  }

  def ingest(layer: String, keyIndexMethod: KeyIndexMethod[K], lsa: LayoutSchemeArg = LayoutSchemeArg.default)
            (implicit cse:
               Case[PolyIngest.type,
                    String ::
                    KeyIndexMethod[K] ::
                    LayoutSchemeArg ::
                    RDD[(I, V)] ::
                    LayerWriter[LayerId] :: HNil]): Unit = {
    conf.set("io.map.index.interval", "1")
    logger.info(s"ingesting tiles into accumulo (${layer})...")
    PolyIngest(layer, keyIndexMethod, lsa, loadTiles, writer)
  }

  def combine(layerId: LayerId)
             (implicit cse: Case[PolyCombine.type, LayerId :: RDD[(K, V)] with Metadata[M] :: HNil]) = {
    logger.info(s"combineLayer ${layerId}...")
    val rdd = read(layerId)
    PolyCombine(layerId, rdd)
  }

  //def validate(layerId: LayerId): Unit

  def validate(layerId: LayerId, dt: Option[DateTime])
              (implicit cse:
                 Case.Aux[
                   PolyValidate.type,
                   TileLayerMetadata[K] ::
                   String  ::
                   LayerId ::
                   Option[DateTime] ::
                   ((LayerId, Option[Extent]) => RDD[(K, V)] with Metadata[M]) :: HNil,
                   (Option[Raster[V]], Option[Raster[V]], List[Raster[V]])],
                      ocse: Case[PolyWrite.type, Option[Raster[V]] :: String :: HNil],
                      lcse: Case[PolyWrite.type, List[Raster[V]] :: String :: HNil]): Unit = {
    val metadata = attributeStore.readMetadata[TileLayerMetadata[K]](layerId)
    val (ingestedRaster, expectedRasterResampled, diffRasters) =
      PolyValidate(metadata, mvValidationTiffLocal, layerId, dt, read _)

    writeRaster(ingestedRaster, s"${validationDir}ingested.${this.getClass.getName}")
    writeRaster(expectedRasterResampled, s"${validationDir}expected.${this.getClass.getName}")
    writeRasters(diffRasters, s"${validationDir}diff.${this.getClass.getName}")
  }

  def ingest(keyIndexMethod: KeyIndexMethod[K])
            (implicit cse:
               Case[
                 PolyIngest.type,
                 String ::
                 KeyIndexMethod[K] ::
                 LayoutSchemeArg ::
                 RDD[(I, V)] ::
                 LayerWriter[LayerId] :: HNil]): Unit = ingest(layerName, keyIndexMethod)
  def combine()(implicit cse: Case[
                                PolyCombine.type,
                                LayerId ::
                                RDD[(K, V)] with Metadata[M] :: HNil]): Unit = combine(LayerId(layerName, zoom))
  def validate(dt: Option[DateTime])(implicit cse: Case.Aux[
                                                     PolyValidate.type,
                                                     TileLayerMetadata[K] ::
                                                     String ::
                                                     LayerId ::
                                                     Option[DateTime] ::
                                                     ((LayerId, Option[Extent]) => RDD[(K, V)] with Metadata[M]) :: HNil,
                                                     (Option[Raster[V]], Option[Raster[V]], List[Raster[V]])],
                                              ocse: Case[PolyWrite.type, Option[Raster[V]] :: String :: HNil],
                                              lcse: Case[PolyWrite.type, List[Raster[V]] :: String :: HNil]): Unit =
    validate(LayerId(layerName, zoom), dt)
  
  def validate()(implicit cse: Case.Aux[
                                PolyValidate.type,
                                TileLayerMetadata[K] ::
                                String ::
                                LayerId ::
                                Option[DateTime] ::
                                ((LayerId, Option[Extent]) => RDD[(K, V)] with Metadata[M]) :: HNil,
                                (Option[Raster[V]], Option[Raster[V]], List[Raster[V]])],
                          ocse: Case[PolyWrite.type, Option[Raster[V]] :: String :: HNil],
                          lcse: Case[PolyWrite.type, List[Raster[V]] :: String :: HNil]): Unit = validate(None)

  def writeRaster[T <: CellGrid](raster: Option[Raster[T]], dir: String)
                                (implicit cse: Case[PolyWrite.type, Option[Raster[T]] :: String :: HNil]): Unit =
    PolyWrite(raster, dir)

  def writeRasters[T <: CellGrid](rasters: List[Raster[T]], dir: String)
                                 (implicit cse: Case[PolyWrite.type, List[Raster[T]] :: String :: HNil]): Unit =
    PolyWrite(rasters, dir)
}
