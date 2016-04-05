package geotrellis.test.multiband.accumulo

import geotrellis.config.json.backend.JCredensials
import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.AccumuloTest
import geotrellis.test.multiband.load.S3Load
import geotrellis.vector.ProjectedExtent
import geotrellis.config.json.dataset.JConfig
import geotrellis.util.S3Support

import org.apache.spark.SparkContext

abstract class S3IngestTest(jConfig: JConfig, jCredensials: JCredensials) extends AccumuloTest[ProjectedExtent, SpatialKey, MultibandTile](jConfig, jCredensials) with S3Support with S3Load

object S3IngestTest {
  def apply(implicit jConfig: JConfig, jCredensials: JCredensials, _sc: SparkContext) = new S3IngestTest(jConfig, jCredensials) {
    @transient implicit val sc = _sc
  }
}
