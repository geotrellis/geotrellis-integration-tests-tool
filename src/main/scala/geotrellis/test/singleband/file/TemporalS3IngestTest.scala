package geotrellis.test.singleband.file

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.FileTest
import geotrellis.test.singleband.load.TemporalS3Load

import org.apache.spark.SparkContext

abstract class TemporalS3IngestTest extends FileTest[TemporalProjectedExtent, SpaceTimeKey, Tile] with TemporalS3Load

object TemporalS3IngestTest {
  def apply(implicit _sc: SparkContext) = new TemporalS3IngestTest {
    @transient implicit val sc = _sc
  }
}
