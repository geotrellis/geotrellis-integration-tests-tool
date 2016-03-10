package geotrellis.test.singleband.hadoop

import geotrellis.test.singleband.load.S3Load
import geotrellis.util.S3Support
import org.apache.spark.SparkContext

class S3IngestTests extends Tests with S3Support with S3Load

object S3IngestTests {
  def apply(implicit sc: SparkContext) = new S3IngestTests()
}
