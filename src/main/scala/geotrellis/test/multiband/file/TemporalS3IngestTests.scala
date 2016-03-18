package geotrellis.test.multiband.file

import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.multiband.load.TemporalS3Load

import org.apache.spark.SparkContext

abstract class TemporalS3IngestTests extends Tests[TemporalProjectedExtent, SpaceTimeKey] with TemporalS3Load

object TemporalS3IngestTests {
  def apply(implicit _sc: SparkContext) = new TemporalS3IngestTests {
    @transient implicit val sc = _sc
  }
}
