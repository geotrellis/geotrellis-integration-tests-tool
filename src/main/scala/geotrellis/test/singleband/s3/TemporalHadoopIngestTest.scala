package geotrellis.test.singleband.s3

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.S3Test
import geotrellis.test.singleband.load.TemporalHadoopLoad

import org.apache.spark.SparkContext
import com.typesafe.config.{Config => TConfig}

abstract class TemporalHadoopIngestTest(implicit configuration: TConfig) extends S3Test[TemporalProjectedExtent, SpaceTimeKey, Tile](configuration) with TemporalHadoopLoad

object TemporalHadoopIngestTest {
  def apply(implicit configuration: TConfig, _sc: SparkContext) = new TemporalHadoopIngestTest {
    @transient implicit val sc = _sc
  }
}