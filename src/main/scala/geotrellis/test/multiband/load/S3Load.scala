package geotrellis.test.multiband.load

import geotrellis.raster.MultibandTile
import geotrellis.spark.SpatialKey
import geotrellis.spark.etl.s3.MultibandGeoTiffS3Input
import geotrellis.test.TestEnvironment
import geotrellis.vector.ProjectedExtent

import org.apache.spark.rdd.RDD

trait S3Load { self: TestEnvironment[ProjectedExtent, SpatialKey, MultibandTile] =>
  val layerName: String = "s3Ingest"
  val zoom: Int = 8

  def loadTiles: RDD[(ProjectedExtent, MultibandTile)] = {
    logger.info("loading tiles from s3...")
    val s3Input = new MultibandGeoTiffS3Input()
    s3Input(Map("bucket" -> s3LoadBucket, "key" -> s3LoadPrefix))
  }
}
