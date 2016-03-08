package geotrellis.test.multiband.load

import geotrellis.spark.etl.s3.TemporalMultibandGeoTiffS3Input
import geotrellis.test.multiband.TemporalTestEnvironment
import geotrellis.util.{S3Support, SparkSupport}
import org.apache.spark.rdd.RDD

trait TemporalS3Load { self: SparkSupport with TemporalTestEnvironment with S3Support  =>
  val layerName: String = "s3TemporalIngest"
  val zoom: Int = 7

  def loadTiles: RDD[(I, V)] = {
    logger.info("loading tiles from s3...")
    val s3Input = new TemporalMultibandGeoTiffS3Input()
    s3Input(s3Params)
  }
}
