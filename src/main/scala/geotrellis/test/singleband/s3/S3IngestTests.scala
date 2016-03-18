package geotrellis.test.singleband.s3

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.S3Tests
import geotrellis.test.singleband.load.S3Load
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext

abstract class S3IngestTests extends S3Tests[ProjectedExtent, SpatialKey, Tile] with S3Load

object S3IngestTests {
  def apply(implicit _sc: SparkContext) = new S3IngestTests {
    @transient implicit val sc = _sc
  }
}
