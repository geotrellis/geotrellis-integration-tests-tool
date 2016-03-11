package geotrellis.test.singleband.accumulo

import geotrellis.test.singleband.load.TemporalHadoopLoad

import org.apache.spark.SparkContext

abstract class TemporalHadoopIngestTests extends TemporalTests with TemporalHadoopLoad

object TemporalHadoopIngestTests {
  def apply(implicit _sc: SparkContext) = new TemporalHadoopIngestTests {
    @transient implicit val sc = _sc
  }
}
