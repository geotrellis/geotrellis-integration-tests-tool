package geotrellis.test.multiband.load

import geotrellis.spark.etl.s3.MultibandGeoTiffS3Input
import geotrellis.test.multiband.SpatialTestEnvironment
import geotrellis.util.{S3Support, SparkSupport}
import org.apache.spark.rdd.RDD

trait S3Load { self: SparkSupport with SpatialTestEnvironment with S3Support  =>
  val layerName: String = "s3Ingest"
  val zoom: Int = 8

  def loadTiles: RDD[(I, V)] = {
    logger.info("loading tiles from s3...")
    val s3Input = new MultibandGeoTiffS3Input()
    s3Input(s3Params)
  }
}
