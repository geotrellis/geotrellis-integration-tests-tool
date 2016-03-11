package geotrellis.test.multiband.hadoop

import geotrellis.test.multiband.load.HadoopLoad
import org.apache.spark.SparkContext

abstract class HadoopIngestTests extends Tests with HadoopLoad

object HadoopIngestTests {
  def apply(implicit _sc: SparkContext) = new HadoopIngestTests {
    @transient implicit val sc = _sc
  }
}
