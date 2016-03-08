package geotrellis.test.singleband.accumulo

import geotrellis.test.singleband.load.TemporalS3Load
import geotrellis.util.S3Support
import org.apache.spark.SparkContext

class TemporalS3IngestTests(@transient implicit val sc: SparkContext) extends TemporalTests with S3Support with TemporalS3Load

object TemporalS3IngestTests {
  def apply(implicit sc: SparkContext) = new TemporalS3IngestTests()
}
