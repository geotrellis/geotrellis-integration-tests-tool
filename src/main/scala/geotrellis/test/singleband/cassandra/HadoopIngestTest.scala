package geotrellis.test.singleband.cassandra

import geotrellis.config.Dataset
import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.CassandraTest
import geotrellis.test.singleband.load.HadoopLoad
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext

abstract class HadoopIngestTest(dataset: Dataset) extends CassandraTest[ProjectedExtent, SpatialKey, Tile](dataset) with HadoopLoad

object HadoopIngestTest {
  def apply(implicit dataset: Dataset, _sc: SparkContext) = new HadoopIngestTest(dataset) {
    @transient implicit val sc = _sc
  }
}
