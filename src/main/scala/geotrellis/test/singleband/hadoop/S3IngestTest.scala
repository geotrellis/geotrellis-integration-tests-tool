package geotrellis.test.singleband.hadoop

import geotrellis.config.Dataset
import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HadoopTest
import geotrellis.test.singleband.load.S3Load
import geotrellis.vector.ProjectedExtent
import geotrellis.util.S3Support

import org.apache.spark.SparkContext

abstract class S3IngestTest(dataset: Dataset) extends HadoopTest[ProjectedExtent, SpatialKey, Tile](dataset) with S3Support with S3Load

object S3IngestTest {
  def apply(implicit dataset: Dataset, _sc: SparkContext) = new S3IngestTest(dataset) {
    @transient implicit val sc = _sc
  }
}
