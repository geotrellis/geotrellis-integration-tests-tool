package geotrellis.test.singleband.s3

import geotrellis.config.json.backend.JCredensials
import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.S3Test
import geotrellis.test.singleband.load.HadoopLoad
import geotrellis.vector.ProjectedExtent
import geotrellis.config.json.dataset.JConfig

import org.apache.spark.SparkContext

abstract class HadoopIngestTest(jConfig: JConfig, jCredensials: JCredensials) extends S3Test[ProjectedExtent, SpatialKey, Tile](jConfig, jCredensials) with HadoopLoad

object HadoopIngestTest {
  def apply(implicit jConfig: JConfig, jCredensials: JCredensials, _sc: SparkContext) = new HadoopIngestTest(jConfig, jCredensials) {
    @transient implicit val sc = _sc
  }
}
