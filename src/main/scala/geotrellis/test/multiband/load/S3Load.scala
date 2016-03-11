package geotrellis.test.multiband.load

import geotrellis.spark.etl.s3.MultibandGeoTiffS3Input
import geotrellis.test.multiband.SpatialTestEnvironment
import geotrellis.vector.ProjectedExtent

import org.apache.spark.rdd.RDD

trait S3Load { self: SpatialTestEnvironment =>
  val layerName: String = "s3Ingest"
  val zoom: Int = 8

  def loadTiles: RDD[(ProjectedExtent, V)] = {
    logger.info("loading tiles from s3...")
    val s3Input = new MultibandGeoTiffS3Input()
    s3Input(s3Params)
  }
}
