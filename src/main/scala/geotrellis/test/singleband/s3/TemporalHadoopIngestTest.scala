package geotrellis.test.singleband.s3

import geotrellis.config.json.backend.JCredentials
import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.S3Test
import geotrellis.test.singleband.load.TemporalHadoopLoad
import geotrellis.config.json.dataset.JConfig
import geotrellis.util.SparkSupport

import org.apache.spark.SparkContext

abstract class TemporalHadoopIngestTest(jConfig: JConfig, jCredentials: JCredentials) extends S3Test[TemporalProjectedExtent, SpaceTimeKey, Tile](jConfig, jCredentials) with TemporalHadoopLoad

object TemporalHadoopIngestTest {
  def apply(implicit jConfig: JConfig, jCredentials: JCredentials, _sc: SparkContext) = new TemporalHadoopIngestTest(jConfig, jCredentials) {
    @transient implicit val sc = SparkSupport.configureTime(jConfig)(_sc)
  }
}
