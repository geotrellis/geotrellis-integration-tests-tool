package geotrellis.test.singleband.s3

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.S3Tests
import geotrellis.test.singleband.load.TemporalS3Load

import org.apache.spark.SparkContext

abstract class TemporalS3IngestTests extends S3Tests[TemporalProjectedExtent, SpaceTimeKey, Tile] with TemporalS3Load

object TemporalS3IngestTests {
  def apply(implicit _sc: SparkContext) = new TemporalS3IngestTests {
    @transient implicit val sc = _sc
  }
}
