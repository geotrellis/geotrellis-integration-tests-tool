package geotrellis.test.singleband.load

import geotrellis.raster.Tile
import geotrellis.spark.SpatialKey
import geotrellis.spark.etl.s3.GeoTiffS3Input
import geotrellis.test.TestEnvironment
import geotrellis.util.S3Support
import geotrellis.vector.ProjectedExtent

import org.apache.spark.rdd.RDD

trait S3Load { self: TestEnvironment[ProjectedExtent, SpatialKey, Tile] with S3Support =>
  def loadTiles: RDD[(ProjectedExtent, Tile)] = {
    logger.info("loading tiles from s3...")
    val s3Input = new GeoTiffS3Input()
    s3Input(etlConf)
  }
}
