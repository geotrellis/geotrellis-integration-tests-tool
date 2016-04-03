package geotrellis.test.multiband.s3

import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.S3Test
import geotrellis.test.multiband.load.TemporalS3Load
import geotrellis.config.json.dataset.JConfig
import geotrellis.util.SparkSupport

import org.apache.spark.SparkContext

abstract class TemporalS3IngestTest(jConfig: JConfig) extends S3Test[TemporalProjectedExtent, SpaceTimeKey, MultibandTile](jConfig) with TemporalS3Load

object TemporalS3IngestTest {
  def apply(implicit jConfig: JConfig, _sc: SparkContext) = new TemporalS3IngestTest(jConfig) {
    @transient implicit val sc = SparkSupport.configureTime(jConfig)(_sc)
  }
}
