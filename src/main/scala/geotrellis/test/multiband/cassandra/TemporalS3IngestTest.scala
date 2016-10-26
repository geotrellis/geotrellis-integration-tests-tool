package geotrellis.test.multiband.cassandra

import geotrellis.config.Dataset
import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.CassandraTest
import geotrellis.test.multiband.load.TemporalS3Load
import geotrellis.util.{S3Support, SparkSupport}
import org.apache.spark.SparkContext

abstract class TemporalS3IngestTest(dataset: Dataset) extends CassandraTest[TemporalProjectedExtent, SpaceTimeKey, MultibandTile](dataset) with S3Support with TemporalS3Load

object TemporalS3IngestTest {
  def apply(implicit dataset: Dataset, _sc: SparkContext) = new TemporalS3IngestTest(dataset) {
    @transient implicit val sc = SparkSupport.configureTime(dataset)(_sc)
  }
}
