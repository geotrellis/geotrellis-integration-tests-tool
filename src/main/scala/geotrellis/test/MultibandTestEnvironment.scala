package geotrellis.test

import geotrellis.core.LayoutSchemeArg
import geotrellis.proj4.WebMercator
import geotrellis.raster._
import geotrellis.raster.io.geotiff.GeoTiff
import geotrellis.raster.io.geotiff.writer.GeoTiffWriter
import geotrellis.spark._
import geotrellis.spark.ingest._
import geotrellis.spark.io._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.index.{KeyIndexMethod, ZCurveKeyIndexMethod}
import geotrellis.spark.tiling.{TilerKeyMethods, ZoomedLayoutScheme}
import geotrellis.util.{HadoopSupport, S3Support, SparkSupport}
import geotrellis.vector.{ProjectedExtent, Extent}
import org.apache.spark.rdd.RDD
import spray.json.JsonFormat

import scala.reflect.ClassTag
import scala.util.Random

abstract class MultibandTestEnvironment[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag
] extends SparkSupport with HadoopSupport with S3Support with Serializable {
  type V = MultibandTile
  type M = TileLayerMetadata[K]

  type TestReader = FilteringLayerReader[LayerId]
  type TestWriter = LayerWriter[LayerId]
  type TestAttributeStore = AttributeStore

  val layerName: String
  val zoom: Int

  val writer: TestWriter
  val reader: TestReader
  val attributeStore: TestAttributeStore

  def loadTiles: RDD[(I, V)]

  def read(layerId: LayerId, extent: Option[Extent] = None): RDD[(K, V)] with Metadata[M] = {
    logger.info(s"reading ${layerId}...")
    extent.fold(reader.read[K, V, M](layerId))(e => reader.read[K, V, M](layerId,  new LayerQuery[K, M].where(Intersects(e))))
  }

  def ingest(layer: String, keyIndexMethod: KeyIndexMethod[K], lsa: LayoutSchemeArg = LayoutSchemeArg.default): Unit = {
    conf.set("io.map.index.interval", "1")

    logger.info(s"ingesting tiles into accumulo (${layer})...")
    MultibandIngest[I, K](loadTiles, lsa.crs, lsa.layoutScheme, pyramid = true) { case (rdd, z) =>
      if (z == 8) {
        if (rdd.filter(!_._2.band(0).isNoDataTile).count != 64) {
          logger.error(s"Incorrect ingest ${layer}")
          throw new Exception(s"Incorrect ingest ${layer}")
        }
      }

      writer.write[K, V, M](LayerId(layer, z), rdd, keyIndexMethod)
    }
  }

  def combine(layerId: LayerId): K = {
    logger.info(s"combineLayer ${layerId}...")
    val rdd = read(layerId)
    val crdd =
      (rdd union rdd)
        .map { case (k, v) => (k, (k, v)) }
        .combineByKey(createTiles[K, V], mergeTiles1[K, V], mergeTiles2[K, V])
        .map { case (key: K, seq: Seq[(K, V)]) =>
          val tiles = seq.map(_._2)
          key -> tiles(0).band(0).combine(tiles(1).band(0))(_ + _)
        }

    crdd.cache()

    val keys = crdd.keys.collect()
    val key  = keys(Random.nextInt(keys.length))

    val ctile = crdd.lookup(key).map(_.toArray).head
    val tile  = rdd.lookup(key).map(t => t.band(0).combine(t.band(0))(_ + _).toArray).head

    if(!ctile.sameElements(tile)) {
      logger.error(s"Incorrect combine layers ${layerId}")
      throw new Exception(s"Incorrect combine layers ${layerId}")
    }

    key
  }

  def validate(layerId: LayerId): Unit

  def ingest(keyIndexMethod: KeyIndexMethod[K]): Unit = ingest(layerName, keyIndexMethod)
  def combine(): K = combine(LayerId(layerName, zoom))
  def validate(): Unit = validate(LayerId(layerName, zoom))

  def writeRaster(raster: Raster[Tile], dir: String): Unit = {
    GeoTiffWriter.write(GeoTiff(raster, WebMercator), s"${dir}.tiff")
    raster.tile.renderPng().write(s"${dir}.png")
  }

  def writeMultibandRaster(raster: Raster[MultibandTile], dir: String): Unit = {
    GeoTiffWriter.write(GeoTiff(raster, WebMercator), s"${dir}.tiff")
  }
}
