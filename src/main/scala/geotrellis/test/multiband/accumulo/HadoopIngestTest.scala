package geotrellis.test.multiband.accumulo

import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.AccumuloTest
import geotrellis.test.multiband.load.HadoopLoad
import geotrellis.vector.ProjectedExtent
import geotrellis.config.json.dataset.JConfig

import org.apache.spark.SparkContext

abstract class HadoopIngestTest(jConfig: JConfig) extends AccumuloTest[ProjectedExtent, SpatialKey, MultibandTile](jConfig) with HadoopLoad

object HadoopIngestTest {
  def apply(implicit jConfig: JConfig, _sc: SparkContext) = new HadoopIngestTest(jConfig) {
    @transient implicit val sc = _sc
  }
}
