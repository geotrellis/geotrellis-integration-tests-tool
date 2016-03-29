package geotrellis.test.singleband.accumulo

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.AccumuloTest
import geotrellis.test.singleband.load.TemporalHadoopLoad

import org.apache.spark.SparkContext
import com.typesafe.config.{Config => TConfig}

abstract class TemporalHadoopIngestTest(implicit configuration: TConfig) extends AccumuloTest[TemporalProjectedExtent, SpaceTimeKey, Tile](configuration) with TemporalHadoopLoad

object TemporalHadoopIngestTest {
  def apply(implicit configuration: TConfig, _sc: SparkContext) = new TemporalHadoopIngestTest {
    @transient implicit val sc = _sc
  }
}
