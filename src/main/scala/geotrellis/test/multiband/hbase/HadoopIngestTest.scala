package geotrellis.test.multiband.hbase

import geotrellis.config.Dataset
import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HBaseTest
import geotrellis.test.multiband.load.HadoopLoad
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext

abstract class HadoopIngestTest(dataset: Dataset) extends HBaseTest[ProjectedExtent, SpatialKey, MultibandTile](dataset) with HadoopLoad

object HadoopIngestTest {
  def apply(implicit dataset: Dataset, _sc: SparkContext) = new HadoopIngestTest(dataset) {
    @transient implicit val sc = _sc
  }
}
