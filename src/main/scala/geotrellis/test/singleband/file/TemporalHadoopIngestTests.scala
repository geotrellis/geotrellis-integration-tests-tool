package geotrellis.test.singleband.file

import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.singleband.load.TemporalHadoopLoad

import org.apache.spark.SparkContext

abstract class TemporalHadoopIngestTests extends Tests[TemporalProjectedExtent, SpaceTimeKey] with TemporalHadoopLoad

object TemporalHadoopIngestTests {
  def apply(implicit _sc: SparkContext) = new TemporalHadoopIngestTests {
    @transient implicit val sc = _sc
  }
}
