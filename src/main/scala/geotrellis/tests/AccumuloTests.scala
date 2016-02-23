package geotrellis.tests

import geotrellis.proj4.WebMercator
import geotrellis.raster.Tile
import geotrellis.spark.ingest._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.tiling.ZoomedLayoutScheme
import geotrellis.spark.{Metadata, LayerId, RasterMetaData, SpatialKey}
import geotrellis.spark.io.accumulo.{AccumuloLayerReader, AccumuloLayerWriter, AccumuloInstance}
import geotrellis.util.{AccumuloSupport, SparkSupport}
import geotrellis.vector.ProjectedExtent
import geotrellis.spark.io.json._
import geotrellis.spark.io.avro.codecs._

import org.apache.spark.rdd.RDD

import scala.util.Random

trait AccumuloTests extends SparkSupport with AccumuloSupport with Serializable {
  type I = ProjectedExtent
  type K = SpatialKey
  type V = Tile
  type M = RasterMetaData

  val layerName: String
  val zoom: Int

  def createTiles(tile: (K, V)): Seq[(K, V)] = Seq(tile)
  def mergeTiles1(tiles: Seq[(K, V)], tile: (K, V)): Seq[(K, V)] = tiles :+ tile
  def mergeTiles2(tiles1: Seq[(K, V)], tiles2: Seq[(K, V)]): Seq[(K, V)] = tiles1 ++ tiles2

  def loadTiles: RDD[(I, V)]

  def spatialIngest(layer: String): Unit = {
    conf.set("io.map.index.interval", "1")

    lazy val writer = AccumuloLayerWriter[K, V, M](instance, table, ZCurveKeyIndexMethod)
    val sourceTiles: RDD[(I, V)] = loadTiles

    logger.info(s"ingesting tiles into accumulo (${layer})...")
    Ingest[I, K](sourceTiles, WebMercator, ZoomedLayoutScheme(WebMercator)) { case (rdd, z) =>
      if (z != 25 && rdd.filter(!_._2.isNoDataTile).count != 58) {
        logger.error(s"Incorrect ingest ${layer}")
        throw new Exception(s"Incorrect ingest ${layer}")
      }

      writer.write(LayerId(layer, z), rdd)
    }
  }

  def read(layerId: LayerId): RDD[(K, V)] with Metadata[M] = {
    logger.info(s"reading ${layerId}...")
    AccumuloLayerReader[K, V, M](instance).read(layerId)
  }

  def validateIngest(layerId: LayerId): Unit =
    if(read(layerId).filter(!_._2.isNoDataTile).count != 58) {
      logger.error(s"Incorrect ingest validation ${layerId}")
      throw new Exception(s"Incorrect ingest validation ${layerId}")
    }
  
  def combineLayers(layerId: LayerId): K = {
    logger.info(s"combineLayer ${layerId}...")
    val rdd = read(layerId)
    val crdd =
      (rdd union rdd)
        .map { case (k, v) => (k, (k, v)) }
        .combineByKey(createTiles, mergeTiles1, mergeTiles2)
        .map { case (key: K, seq: Seq[(K, V)]) =>
          val tiles = seq.map(_._2)
          key -> tiles(0).combine(tiles(1))(_ + _)
        }

    crdd.cache()

    val keys = crdd.keys.collect()
    val key = keys(Random.nextInt(keys.length))

    val ctile = crdd.lookup(key).map(_.toArray).head
    val tile = rdd.lookup(key).map(t => t.combine(t)(_ + _).toArray).head

    if(!ctile.sameElements(tile)) {
      logger.error(s"Incorrect combine layers ${layerId}")
      throw new Exception(s"Incorrect combine layers ${layerId}")
    }

    key
  }
}
