package geotrellis.test.multiband.hadoop

import geotrellis.config.json.backend.JCredentials
import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HadoopTest
import geotrellis.test.multiband.load.TemporalS3Load
import geotrellis.config.json.dataset.JConfig
import geotrellis.util.{S3Support, SparkSupport}

import org.apache.spark.SparkContext

abstract class TemporalS3IngestTest(jConfig: JConfig, jCredentials: JCredentials) extends HadoopTest[TemporalProjectedExtent, SpaceTimeKey, MultibandTile](jConfig, jCredentials) with S3Support with TemporalS3Load

object TemporalS3IngestTest {
  def apply(implicit jConfig: JConfig, jCredentials: JCredentials, _sc: SparkContext) = new TemporalS3IngestTest(jConfig, jCredentials) {
    @transient implicit val sc = SparkSupport.configureTime(jConfig)(_sc)
  }
}
