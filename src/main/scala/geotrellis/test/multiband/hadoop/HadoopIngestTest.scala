package geotrellis.test.multiband.hadoop

import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HadoopTest
import geotrellis.test.multiband.load.HadoopLoad
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext

abstract class HadoopIngestTest extends HadoopTest[ProjectedExtent, SpatialKey, MultibandTile] with HadoopLoad

object HadoopIngestTest {
  def apply(implicit _sc: SparkContext) = new HadoopIngestTest {
    @transient implicit val sc = _sc
  }
}
