package geotrellis.test.multiband.s3

import geotrellis.config.json.backend.JCredensials
import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.S3Test
import geotrellis.test.multiband.load.TemporalHadoopLoad
import geotrellis.config.json.dataset.JConfig
import geotrellis.util.SparkSupport

import org.apache.spark.SparkContext

abstract class TemporalHadoopIngestTest(jConfig: JConfig, jCredensials: JCredensials) extends S3Test[TemporalProjectedExtent, SpaceTimeKey, MultibandTile](jConfig, jCredensials) with TemporalHadoopLoad

object TemporalHadoopIngestTest {
  def apply(implicit jConfig: JConfig, jCredensials: JCredensials, _sc: SparkContext) = new TemporalHadoopIngestTest(jConfig, jCredensials) {
    @transient implicit val sc = SparkSupport.configureTime(jConfig)(_sc)
  }
}
