package geotrellis.test.multiband.cassandra

import geotrellis.config.Dataset
import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.CassandraTest
import geotrellis.test.multiband.load.S3Load
import geotrellis.util.S3Support
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext

abstract class S3IngestTest(dataset: Dataset) extends CassandraTest[ProjectedExtent, SpatialKey, MultibandTile](dataset) with S3Support with S3Load

object S3IngestTest {
  def apply(implicit dataset: Dataset, _sc: SparkContext) = new S3IngestTest(dataset) {
    @transient implicit val sc = _sc
  }
}
