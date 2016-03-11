package geotrellis.test.singleband.file

import geotrellis.test.singleband.load.HadoopLoad

import org.apache.spark.SparkContext

abstract class HadoopIngestTests extends Tests with HadoopLoad

object HadoopIngestTests {
  def apply(implicit _sc: SparkContext) = new HadoopIngestTests {
    @transient implicit val sc = _sc
  }
}
