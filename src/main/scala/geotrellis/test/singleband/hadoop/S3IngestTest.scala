package geotrellis.test.singleband.hadoop

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HadoopTest
import geotrellis.test.singleband.load.S3Load
import geotrellis.vector.ProjectedExtent
import geotrellis.config.json.dataset.JConfig
import geotrellis.util.S3Support

import org.apache.spark.SparkContext

abstract class S3IngestTest(jConfig: JConfig) extends HadoopTest[ProjectedExtent, SpatialKey, Tile](jConfig) with S3Support with S3Load

object S3IngestTest {
  def apply(implicit jConfig: JConfig, _sc: SparkContext) = new S3IngestTest(jConfig) {
    @transient implicit val sc = _sc
  }
}
