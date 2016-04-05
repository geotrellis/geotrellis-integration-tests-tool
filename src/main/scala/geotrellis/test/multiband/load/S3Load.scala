package geotrellis.test.multiband.load

import geotrellis.raster.MultibandTile
import geotrellis.spark.SpatialKey
import geotrellis.spark.etl.s3.MultibandGeoTiffS3Input
import geotrellis.test.TestEnvironment
import geotrellis.util.S3Support
import geotrellis.vector.ProjectedExtent

import org.apache.spark.rdd.RDD

trait S3Load { self: TestEnvironment[ProjectedExtent, SpatialKey, MultibandTile] with S3Support =>
  def loadTiles: RDD[(ProjectedExtent, MultibandTile)] = {
    logger.info("loading tiles from s3...")
    val s3Input = new MultibandGeoTiffS3Input()
    s3Input(loadParams)
  }
}
