package geotrellis.test.singleband.hadoop

import geotrellis.test.singleband.load.TemporalHadoopLoad
import geotrellis.util.{HadoopSupport, S3Support}
import org.apache.spark.SparkContext

class TemporalHadoopIngestTests(@transient implicit val sc: SparkContext) extends TemporalTests with HadoopSupport with S3Support with TemporalHadoopLoad

object TemporalHadoopIngestTests {
  def apply(implicit sc: SparkContext) = new TemporalHadoopIngestTests()
}
