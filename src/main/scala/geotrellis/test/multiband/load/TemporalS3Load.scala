package geotrellis.test.multiband.load

import geotrellis.raster.MultibandTile
import geotrellis.spark.{SpaceTimeKey, TemporalProjectedExtent}
import geotrellis.spark.etl.s3.TemporalMultibandGeoTiffS3Input
import geotrellis.test.TestEnvironment
import geotrellis.util.S3Support

import org.apache.spark.rdd.RDD

trait TemporalS3Load { self: TestEnvironment[TemporalProjectedExtent, SpaceTimeKey, MultibandTile] with S3Support =>
  val zoom: Int = 8

  def loadTiles: RDD[(TemporalProjectedExtent, MultibandTile)] = {
    logger.info("loading tiles from s3...")
    val s3Input = new TemporalMultibandGeoTiffS3Input()
    s3Input(loadParams)
  }
}
