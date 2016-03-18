package geotrellis.test.multiband.hadoop

import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HadoopTests
import geotrellis.test.multiband.load.TemporalHadoopLoad

import org.apache.spark.SparkContext

abstract class TemporalHadoopIngestTests extends HadoopTests[TemporalProjectedExtent, SpaceTimeKey, MultibandTile] with TemporalHadoopLoad

object TemporalHadoopIngestTests {
  def apply(implicit _sc: SparkContext) = new TemporalHadoopIngestTests {
    @transient implicit val sc = _sc
  }
}
