package geotrellis.test.multiband.hadoop

import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HadoopTests
import geotrellis.test.multiband.load.TemporalS3Load

import org.apache.spark.SparkContext

abstract class TemporalS3IngestTests extends HadoopTests[TemporalProjectedExtent, SpaceTimeKey, MultibandTile] with TemporalS3Load

object TemporalS3IngestTests {
  def apply(implicit _sc: SparkContext) = new TemporalS3IngestTests {
    @transient implicit val sc = _sc
  }
}
