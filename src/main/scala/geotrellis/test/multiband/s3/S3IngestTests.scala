package geotrellis.test.multiband.s3

import geotrellis.test.multiband.load.S3Load
import org.apache.spark.SparkContext

abstract class S3IngestTests extends Tests with S3Load

object S3IngestTests {
  def apply(implicit _sc: SparkContext) = new S3IngestTests {
    @transient implicit val sc = _sc
  }
}
