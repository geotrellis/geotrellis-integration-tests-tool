package geotrellis.test

import geotrellis.spark.etl.s3.GeoTiffS3Input
import geotrellis.util.{SparkSupport, S3Support}
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
