package geotrellis.test.multiband.hadoop

import geotrellis.test.multiband.load.HadoopLoad
import geotrellis.util.{HadoopSupport, S3Support}
import org.apache.spark.SparkContext

class HadoopIngestTests(@transient implicit val sc: SparkContext) extends Tests with HadoopSupport with S3Support with HadoopLoad

object HadoopIngestTests {
  def apply(implicit sc: SparkContext) = new HadoopIngestTests()
}
