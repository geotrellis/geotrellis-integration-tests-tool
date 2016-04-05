package geotrellis.test.singleband.hadoop

import geotrellis.config.json.backend.JCredensials
import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HadoopTest
import geotrellis.test.singleband.load.TemporalS3Load
import geotrellis.config.json.dataset.JConfig
import geotrellis.util.{S3Support, SparkSupport}

import org.apache.spark.SparkContext

abstract class TemporalS3IngestTest(jConfig: JConfig, jCredensials: JCredensials) extends HadoopTest[TemporalProjectedExtent, SpaceTimeKey, Tile](jConfig, jCredensials) with S3Support with TemporalS3Load

object TemporalS3IngestTest {
  def apply(implicit jConfig: JConfig, jCredensials: JCredensials, _sc: SparkContext) = new TemporalS3IngestTest(jConfig, jCredensials) {
    @transient implicit val sc = SparkSupport.configureTime(jConfig)(_sc)
  }
}
