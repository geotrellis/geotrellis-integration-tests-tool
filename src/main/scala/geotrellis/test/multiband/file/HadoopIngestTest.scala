package geotrellis.test.multiband.file

import geotrellis.config.json.backend.JCredensials
import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.FileTest
import geotrellis.test.multiband.load.HadoopLoad
import geotrellis.vector.ProjectedExtent
import geotrellis.config.json.dataset.JConfig

import org.apache.spark.SparkContext

abstract class HadoopIngestTest(jConfig: JConfig, jCredensials: JCredensials) extends FileTest[ProjectedExtent, SpatialKey, MultibandTile](jConfig, jCredensials) with HadoopLoad

object HadoopIngestTest {
  def apply(implicit jConfig: JConfig, jCredensials: JCredensials, _sc: SparkContext) = new HadoopIngestTest(jConfig, jCredensials) {
    @transient implicit val sc = _sc
  }
}
