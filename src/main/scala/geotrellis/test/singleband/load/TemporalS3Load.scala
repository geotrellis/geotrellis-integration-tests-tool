package geotrellis.test.singleband.load

import geotrellis.raster.Tile
import geotrellis.spark.{SpaceTimeKey, TemporalProjectedExtent}
import geotrellis.spark.etl.s3.TemporalGeoTiffS3Input
import geotrellis.test.TestEnvironment
import geotrellis.util.S3Support
import org.apache.spark.rdd.RDD

trait TemporalS3Load { self: TestEnvironment[TemporalProjectedExtent, SpaceTimeKey, Tile] with S3Support =>
  def loadTiles: RDD[(TemporalProjectedExtent, Tile)] = {
    logger.info("loading tiles from s3...")
    val s3Input = new TemporalGeoTiffS3Input()
    s3Input(loadParams)
  }
}
