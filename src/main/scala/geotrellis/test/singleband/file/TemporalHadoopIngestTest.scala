package geotrellis.test.singleband.file

import geotrellis.config.json.backend.JCredensials
import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.FileTest
import geotrellis.test.singleband.load.TemporalHadoopLoad
import geotrellis.config.json.dataset.JConfig
import geotrellis.util.SparkSupport

import org.apache.spark.SparkContext

abstract class TemporalHadoopIngestTest(jConfig: JConfig, jCredensials: JCredensials) extends FileTest[TemporalProjectedExtent, SpaceTimeKey, Tile](jConfig, jCredensials) with TemporalHadoopLoad

object TemporalHadoopIngestTest {
  def apply(implicit jConfig: JConfig, jCredensials: JCredensials, _sc: SparkContext) = new TemporalHadoopIngestTest(jConfig, jCredensials) {
    @transient implicit val sc = SparkSupport.configureTime(jConfig)(_sc)
  }
}
