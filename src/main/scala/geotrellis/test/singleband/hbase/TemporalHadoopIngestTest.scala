package geotrellis.test.singleband.hbase

import geotrellis.config.Dataset
import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HBaseTest
import geotrellis.test.singleband.load.TemporalHadoopLoad
import geotrellis.util.SparkSupport

import org.apache.spark.SparkContext

abstract class TemporalHadoopIngestTest(dataset: Dataset) extends HBaseTest[TemporalProjectedExtent, SpaceTimeKey, Tile](dataset) with TemporalHadoopLoad

object TemporalHadoopIngestTest {
  def apply(implicit dataset: Dataset, _sc: SparkContext) = new TemporalHadoopIngestTest(dataset) {
    @transient implicit val sc = SparkSupport.configureTime(dataset)(_sc)
  }
}
