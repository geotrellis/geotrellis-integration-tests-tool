package geotrellis.test.multiband.file

import geotrellis.config.json.backend.JCredensials
import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.FileTest
import geotrellis.test.multiband.load.TemporalS3Load
import geotrellis.config.json.dataset.JConfig
import geotrellis.util.{S3Support, SparkSupport}

import org.apache.spark.SparkContext

abstract class TemporalS3IngestTest(jConfig: JConfig, jCredensials: JCredensials) extends FileTest[TemporalProjectedExtent, SpaceTimeKey, MultibandTile](jConfig, jCredensials) with S3Support with TemporalS3Load

object TemporalS3IngestTest {
  def apply(implicit jConfig: JConfig, jCredensials: JCredensials, _sc: SparkContext) = new TemporalS3IngestTest(jConfig, jCredensials) {
    @transient implicit val sc = SparkSupport.configureTime(jConfig)(_sc)
  }
}
