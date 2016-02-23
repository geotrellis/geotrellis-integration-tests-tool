package geotrellis.test.s3

import geotrellis.spark.etl.s3.GeoTiffS3Input
import geotrellis.test.TestEnvironment
import geotrellis.util.{S3Support, SparkSupport}
import org.apache.spark.rdd.RDD

trait S3Load { self: SparkSupport with TestEnvironment with S3Support  =>
  val layerName: String = "s3Ingest"
  val zoom: Int = 20

  def loadTiles: RDD[(I, V)] = {
    logger.info("loading tiles from s3...")
    val s3Input = new GeoTiffS3Input()
    s3Input(s3Params)
  }
}
