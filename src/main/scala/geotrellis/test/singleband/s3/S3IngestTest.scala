package geotrellis.test.singleband.s3

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.S3Test
import geotrellis.test.singleband.load.S3Load
import geotrellis.vector.ProjectedExtent
import geotrellis.config.json.dataset.JConfig

import org.apache.spark.SparkContext

abstract class S3IngestTest(jConfig: JConfig) extends S3Test[ProjectedExtent, SpatialKey, Tile](jConfig) with S3Load

object S3IngestTest {
  def apply(implicit jConfig: JConfig, _sc: SparkContext) = new S3IngestTest(jConfig) {
    @transient implicit val sc = _sc
  }
}
