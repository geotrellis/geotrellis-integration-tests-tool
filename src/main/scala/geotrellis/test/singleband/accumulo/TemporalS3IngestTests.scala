package geotrellis.test.singleband.accumulo

import geotrellis.test.singleband.load.TemporalS3Load

import org.apache.spark.SparkContext

abstract class TemporalS3IngestTests extends TemporalTests with TemporalS3Load

object TemporalS3IngestTests {
  def apply(implicit _sc: SparkContext) = new TemporalS3IngestTests {
    @transient implicit val sc = _sc
  }
}
