package geotrellis.test.singleband.load

import geotrellis.spark.etl.s3.GeoTiffS3Input
import geotrellis.test.singleband.SpatialTestEnvironment
import geotrellis.vector.ProjectedExtent

import org.apache.spark.rdd.RDD

trait S3Load { self: SpatialTestEnvironment =>
  val layerName: String = "s3Ingest"
  val zoom: Int = 8

  def loadTiles: RDD[(ProjectedExtent, V)] = {
    logger.info("loading tiles from s3...")
    val s3Input = new GeoTiffS3Input()
    s3Input(s3Params)
  }
}
