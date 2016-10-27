package geotrellis.test.singleband.s3

import geotrellis.config.Dataset
import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.S3Test
import geotrellis.test.singleband.load.TemporalS3Load
import geotrellis.util.SparkSupport

import org.apache.spark.SparkContext

abstract class TemporalS3IngestTest(dataset: Dataset) extends S3Test[TemporalProjectedExtent, SpaceTimeKey, Tile](dataset) with TemporalS3Load

object TemporalS3IngestTest {
  def apply(implicit dataset: Dataset, _sc: SparkContext) = new TemporalS3IngestTest(dataset) {
    @transient implicit val sc = SparkSupport.configureTime(dataset)(_sc)
  }
}
