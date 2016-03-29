package geotrellis.test.multiband.s3

import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.S3Test
import geotrellis.test.multiband.load.TemporalHadoopLoad

import org.apache.spark.SparkContext
import com.typesafe.config.{Config => TConfig}

abstract class TemporalHadoopIngestTest(implicit configuration: TConfig) extends S3Test[TemporalProjectedExtent, SpaceTimeKey, MultibandTile](configuration) with TemporalHadoopLoad

object TemporalHadoopIngestTest {
  def apply(implicit configuration: TConfig, _sc: SparkContext) = new TemporalHadoopIngestTest {
    @transient implicit val sc = _sc
  }
}
