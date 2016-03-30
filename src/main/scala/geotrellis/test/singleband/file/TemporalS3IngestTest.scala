package geotrellis.test.singleband.file

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.FileTest
import geotrellis.test.singleband.load.TemporalS3Load
import org.apache.spark.SparkContext
import com.typesafe.config.{Config => TConfig}
import geotrellis.util.S3Support

abstract class TemporalS3IngestTest(implicit configuration: TConfig) extends FileTest[TemporalProjectedExtent, SpaceTimeKey, Tile](configuration) with S3Support with TemporalS3Load

object TemporalS3IngestTest {
  def apply(implicit configuration: TConfig, _sc: SparkContext) = new TemporalS3IngestTest {
    @transient implicit val sc = _sc
  }
}
