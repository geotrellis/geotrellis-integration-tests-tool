package geotrellis.test.multiband.file

import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.FileTest
import geotrellis.test.multiband.load.TemporalS3Load
import geotrellis.config.json.dataset.JConfig
import geotrellis.util.{S3Support, SparkSupport}

import org.apache.spark.SparkContext

abstract class TemporalS3IngestTest(jConfig: JConfig) extends FileTest[TemporalProjectedExtent, SpaceTimeKey, MultibandTile](jConfig) with S3Support with TemporalS3Load

object TemporalS3IngestTest {
  def apply(implicit jConfig: JConfig, _sc: SparkContext) = new TemporalS3IngestTest(jConfig) {
    @transient implicit val sc = SparkSupport.configureTime(jConfig)(_sc)
  }
}
