package geotrellis.tests

import geotrellis.accumulo.AccumuloSupport
import geotrellis.hadoop.HadoopSupport
import geotrellis.proj4.WebMercator
import geotrellis.raster.Tile
import geotrellis.s3.S3Support
import geotrellis.spark.ingest._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.tiling.ZoomedLayoutScheme
import geotrellis.spark.{LayerId, RasterMetaData, SpatialKey}
import geotrellis.spark.io.accumulo.{AccumuloLayerReader, AccumuloLayerWriter, AccumuloInstance}
import geotrellis.util.SparkSupport
import geotrellis.vector.ProjectedExtent
import geotrellis.spark.io.json._
import geotrellis.spark.io.avro.codecs._
import geotrellis.spark.render._

import org.apache.spark.rdd.RDD

import scala.util.Random

trait AccumuloTests extends SparkSupport with S3Support with AccumuloSupport with HadoopSupport with Serializable {
  type I = ProjectedExtent
  type K = SpatialKey
  type V = Tile
  type M = RasterMetaData

  val layerName: String

  @transient lazy val instance = AccumuloInstance(instanceName, zookeeper, user, token)

  // Functions for combine step
  def createTiles(tile: (K, V)): Seq[(K, V)] = Seq(tile)
  def mergeTiles1(tiles: Seq[(K, V)], tile: (K, V)): Seq[(K, V)] = tiles :+ tile
  def mergeTiles2(tiles1: Seq[(K, V)], tiles2: Seq[(K, V)]): Seq[(K, V)] = tiles1 ++ tiles2

  def loadTiles: RDD[(I, V)]

  def spatialIngest(layer: String) = {
    conf.set("io.map.index.interval", "1")

    lazy val writer = AccumuloLayerWriter[K, V, M](instance, layer, ZCurveKeyIndexMethod)
    val sourceTiles: RDD[(I, V)] = loadTiles

    println(s"ingesting tiles into accumulo (${layer})...")
    Ingest[I, K](sourceTiles, WebMercator, ZoomedLayoutScheme(WebMercator)) { case (rdd, zoom) =>
      if (zoom != 25 && rdd.filter(!_._2.isNoDataTile).count != 58)
        throw new Exception("Incorrect ingest")

      writer.write(LayerId(layer, zoom), rdd)
    }
  }

  def read(layerId: LayerId) = {
    println(s"reading ${layerId}...")
    AccumuloLayerReader[K, V, M](instance).read(layerId)
  }

  def validateIngest(layerId: LayerId) =
    if(read(layerId).filter(!_._2.isNoDataTile).count != 58)
      throw new Exception("Incorrect ingest validation")
  
  def combineLayers(layerId: LayerId) = {
    println(s"combineLayer ${layerId}...")
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

    if(!ctile.sameElements(tile))
      throw new Exception("Incorrect combine layers")
  }

  def save(layerId: LayerId) = {
    read(layerId).renderPng(layerId, "file:/tmp/{name}/{z}/{x}/{y}.png")
  }

}
