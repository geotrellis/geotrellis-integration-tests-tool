package geotrellis.test

import geotrellis.proj4.WebMercator
import geotrellis.raster._
import geotrellis.raster.io.geotiff.GeoTiff
import geotrellis.raster.io.geotiff.writer.GeoTiffWriter
import geotrellis.spark._
import geotrellis.spark.ingest._
import geotrellis.spark.io._
import geotrellis.spark.tiling.{TilerKeyMethods, ZoomedLayoutScheme}
import geotrellis.util.{HadoopSupport, S3Support, SparkSupport}
import geotrellis.vector.Extent
import org.apache.spark.rdd.RDD
import spray.json.JsonFormat

import scala.reflect.ClassTag
import scala.util.Random

abstract class MultibandTestEnvironment[
  I: ProjectedExtentComponent: ClassTag: ? => TilerKeyMethods[I, K],
  K: SpatialComponent: Boundable: ClassTag
] extends SparkSupport with HadoopSupport with S3Support with Serializable {
  type V = MultiBandTile
  type M = RasterMetaData

  type TestReader = FilteringLayerReader[LayerId, K, M, RDD[(K, V)] with Metadata[M]]
  type TestWriter = Writer[LayerId, RDD[(K, V)] with Metadata[M]]
  type TestAttributeStore = AttributeStore[JsonFormat]

  val layerName: String
  val zoom: Int

  val writer: TestWriter
  val reader: TestReader
  val attributeStore: TestAttributeStore

  def loadTiles: RDD[(I, V)]

  def read(layerId: LayerId, extent: Option[Extent] = None): RDD[(K, V)] with Metadata[M] = {
    logger.info(s"reading ${layerId}...")
    extent.fold(reader.read(layerId))(e => reader.read(layerId,  new RDDQuery[K, M].where(Intersects(e))))
  }

  def ingest(layer: String): Unit = {
    conf.set("io.map.index.interval", "1")

    logger.info(s"ingesting tiles into accumulo (${layer})...")
    FMultiBandIngest[I, K](loadTiles, WebMercator, ZoomedLayoutScheme(WebMercator), pyramid = true) { case (rdd, z) =>
      if (z == 8) {
        if (rdd.filter(!_._2.band(0).isNoDataTile).count != 64) {
          logger.error(s"Incorrect ingest ${layer}")
          throw new Exception(s"Incorrect ingest ${layer}")
        }
      }

      writer.write(LayerId(layer, z), rdd)
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

  def ingest(): Unit = ingest(layerName)
  def combine(): K = combine(LayerId(layerName, zoom))
  def validate(): Unit = validate(LayerId(layerName, zoom))

  def writeRaster(raster: Raster[Tile], dir: String): Unit = {
    GeoTiffWriter.write(GeoTiff(raster, WebMercator), s"${dir}.tiff")
    raster.tile.renderPng().write(s"${dir}.png")
  }

  def writeMultiBandRaster(raster: Raster[MultiBandTile], dir: String): Unit = {
    GeoTiffWriter.write(GeoTiff(raster, WebMercator), s"${dir}.tiff")
  }
}
