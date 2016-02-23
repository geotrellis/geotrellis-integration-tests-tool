package geotrellis.test.accumulo

import geotrellis.spark.LayerId
import geotrellis.spark.etl.s3.GeoTiffS3Input
import geotrellis.util.S3Support

import org.apache.spark.rdd.RDD

class S3IngestAccumuloTests extends AccumuloTests with S3Support {
  val layerName: String = "s3ingest-accumulo"
  val zoom: Int = 20

  def loadTiles: RDD[(I, V)] = {
    logger.info("loading tiles from s3...")
    val s3Input = new GeoTiffS3Input()
    s3Input(s3Params)
  }

  def spatialIngest: Unit = spatialIngest(layerName)
  def combineLayers: Unit = combineLayers(LayerId(layerName, zoom))
}
