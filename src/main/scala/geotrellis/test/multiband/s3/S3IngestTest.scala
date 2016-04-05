package geotrellis.test.multiband.s3

import geotrellis.config.json.backend.JCredensials
import geotrellis.config.json.dataset.JConfig
import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.S3Test
import geotrellis.test.multiband.load.S3Load
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext

abstract class S3IngestTest(jConfig: JConfig, jCredensials: JCredensials) extends S3Test[ProjectedExtent, SpatialKey, MultibandTile](jConfig, jCredensials) with S3Load

object S3IngestTest {
  def apply(implicit jConfig: JConfig, jCredensials: JCredensials, _sc: SparkContext) = new S3IngestTest(jConfig, jCredensials) {
    @transient implicit val sc = _sc
  }
}
