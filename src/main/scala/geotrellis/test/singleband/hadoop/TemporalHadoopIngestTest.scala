package geotrellis.test.singleband.hadoop

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HadoopTest
import geotrellis.test.singleband.load.TemporalHadoopLoad
import geotrellis.config.json.dataset.JConfig
import geotrellis.util.SparkSupport

import org.apache.spark.SparkContext

abstract class TemporalHadoopIngestTest(jConfig: JConfig) extends HadoopTest[TemporalProjectedExtent, SpaceTimeKey, Tile](jConfig) with TemporalHadoopLoad

object TemporalHadoopIngestTest {
  def apply(implicit jConfig: JConfig, _sc: SparkContext) = new TemporalHadoopIngestTest(jConfig) {
    @transient implicit val sc = SparkSupport.configureTime(jConfig)(_sc)
  }
}
