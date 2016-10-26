package geotrellis.test.multiband.hadoop

import geotrellis.config.Dataset
import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HadoopTest
import geotrellis.test.multiband.load.TemporalHadoopLoad
import geotrellis.util.SparkSupport

import org.apache.spark.SparkContext

abstract class TemporalHadoopIngestTest(dataset: Dataset) extends HadoopTest[TemporalProjectedExtent, SpaceTimeKey, MultibandTile](dataset) with TemporalHadoopLoad

object TemporalHadoopIngestTest {
  def apply(implicit dataset: Dataset, _sc: SparkContext) = new TemporalHadoopIngestTest(dataset) {
    @transient implicit val sc = SparkSupport.configureTime(dataset)(_sc)
  }
}
