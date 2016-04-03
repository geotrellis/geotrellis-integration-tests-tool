package geotrellis.test.singleband.hadoop

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HadoopTest
import geotrellis.test.singleband.load.HadoopLoad
import geotrellis.vector.ProjectedExtent
import geotrellis.config.json.dataset.JConfig

import org.apache.spark.SparkContext

abstract class HadoopIngestTest(jConfig: JConfig) extends HadoopTest[ProjectedExtent, SpatialKey, Tile](jConfig) with HadoopLoad

object HadoopIngestTest {
  def apply(implicit jConfig: JConfig, _sc: SparkContext) = new HadoopIngestTest(jConfig) {
    @transient implicit val sc = _sc
  }
}
