package geotrellis.test.multiband.hadoop

import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HadoopTest
import geotrellis.test.multiband.load.TemporalS3Load

import org.apache.spark.SparkContext
import com.typesafe.config.{Config => TConfig}

abstract class TemporalS3IngestTest(implicit configuration: TConfig) extends HadoopTest[TemporalProjectedExtent, SpaceTimeKey, MultibandTile](configuration) with TemporalS3Load

object TemporalS3IngestTest {
  def apply(implicit configuration: TConfig, _sc: SparkContext) = new TemporalS3IngestTest {
    @transient implicit val sc = _sc
  }
}
