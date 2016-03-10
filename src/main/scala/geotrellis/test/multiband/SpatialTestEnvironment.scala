package geotrellis.test.multiband

import geotrellis.proj4.WebMercator
import geotrellis.raster._
import geotrellis.raster.io.geotiff.MultiBandGeoTiff
import geotrellis.spark.ingest._
import geotrellis.core._
import geotrellis.spark.io.AttributeStore.Fields
import geotrellis.spark.io._
import geotrellis.spark.tiling.ZoomedLayoutScheme
import geotrellis.spark.{LayerId, Metadata, RasterMetaData, SpatialKey}
import geotrellis.test.TestEnvironment
import geotrellis.test._
import geotrellis.util.{HadoopSupport, SparkSupport}
import geotrellis.vector.{Extent, ProjectedExtent}
import org.apache.spark.rdd.RDD
import spray.json.JsonFormat

import scala.util.Random

trait SpatialTestEnvironment extends TestEnvironment { self: SparkSupport with HadoopSupport =>
  type I = ProjectedExtent
  type K = SpatialKey
  type V = MultiBandTile
  type M = RasterMetaData

  def loadTiles: RDD[(I, V)]

  val writer: Writer[LayerId, RDD[(K, V)] with Metadata[M]]
  val reader: FilteringLayerReader[LayerId, K, M, RDD[(K, V)] with Metadata[M]]
  val attributeStore: AttributeStore[JsonFormat]

  def ingest(layer: String): Unit = {
    conf.set("io.map.index.interval", "1")

    logger.info(s"ingesting tiles into accumulo (${layer})...")
    MultiBandIngest[I, K](loadTiles, WebMercator, ZoomedLayoutScheme(WebMercator), pyramid = true) { case (rdd, z) =>
      if (z == 8) {
        if (rdd.filter(!_._2.band(0).isNoDataTile).count != 64) {
          logger.error(s"Incorrect ingest ${layer}")
          throw new Exception(s"Incorrect ingest ${layer}")
        }
      }

      writer.write(LayerId(layer, z), rdd)
    }
  }

  def read(layerId: LayerId, extent: Option[Extent] = None): RDD[(K, V)] with Metadata[M] = {
    logger.info(s"reading ${layerId}...")
    extent.fold(reader.read(layerId))(e => reader.read(layerId,  new RDDQuery[K, M].where(Intersects(e))))
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

  def validate(layerId: LayerId): Unit = {
    val metadata = attributeStore.readLayerAttribute[RasterMetaData](layerId, Fields.metaData)
    val expected = MultiBandGeoTiff(mvValidationTiffLocal)
    val expectedRaster = expected.raster.reproject(expected.crs, metadata.crs)

    val ingestedRaster =
      read(layerId, Some(expectedRaster.extent))
        .stitch
        .crop(expectedRaster.extent)

    val expectedRasterResampled = expectedRaster.resample(ingestedRaster.rasterExtent)
    for (i <- 0 to expectedRaster.bandCount) {
      val diffArr =
        ingestedRaster
          .band(i)
          .toArray
          .zip(expectedRasterResampled.band(i).toArray)
          .map { case (v1, v2) => v1 - v2 }
      val diffRaster = Raster(ArrayTile(diffArr, ingestedRaster.cols, ingestedRaster.rows), ingestedRaster.extent)

      println(s"band($i) validation: ${ingestedRaster.band(i).toArray().sameElements(expectedRasterResampled.band(i).toArray())}")

      writeRaster(diffRaster, s"${validationDir}diff.$i.${this.getClass.getName}")
    }

    writeMultiBandRaster(ingestedRaster, s"${validationDir}ingested.${this.getClass.getName}")
    writeMultiBandRaster(expectedRasterResampled, s"${validationDir}expected.${this.getClass.getName}")

    println(s"validation.size.eq: ${ingestedRaster.tile.size == expectedRasterResampled.tile.size}")
  }
}
