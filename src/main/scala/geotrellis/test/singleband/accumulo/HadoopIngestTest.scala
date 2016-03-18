package geotrellis.test.singleband.accumulo

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.AccumuloTest
import geotrellis.test.singleband.load.HadoopLoad
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext

abstract class HadoopIngestTest extends AccumuloTest[ProjectedExtent, SpatialKey, Tile] with HadoopLoad

object HadoopIngestTest {
  def apply(implicit _sc: SparkContext) = new HadoopIngestTest {
    @transient implicit val sc = _sc
  }
}
