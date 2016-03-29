package geotrellis.test.multiband.load

import geotrellis.raster.MultibandTile
import geotrellis.spark.{SpaceTimeKey, TemporalProjectedExtent}
import geotrellis.spark.etl.s3.TemporalMultibandGeoTiffS3Input
import geotrellis.test.TestEnvironment

import org.apache.spark.rdd.RDD

trait TemporalS3Load { self: TestEnvironment[TemporalProjectedExtent, SpaceTimeKey, MultibandTile] =>
  val layerName: String = "s3Ingest"
  val zoom: Int = 8

  def loadTiles: RDD[(TemporalProjectedExtent, MultibandTile)] = {
    logger.info("loading tiles from s3...")
    val s3Input = new TemporalMultibandGeoTiffS3Input()
    s3Input(Map("bucket" -> s3LoadBucket, "key" -> s3LoadPrefix))
  }
}
