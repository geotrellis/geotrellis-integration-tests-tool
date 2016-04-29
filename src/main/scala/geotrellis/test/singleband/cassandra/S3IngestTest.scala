package geotrellis.test.singleband.cassandra

import geotrellis.config.json.backend.JCredentials
import geotrellis.config.json.dataset.JConfig
import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.CassandraTest
import geotrellis.test.singleband.load.S3Load
import geotrellis.util.S3Support
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext

abstract class S3IngestTest(jConfig: JConfig, jCredentials: JCredentials) extends CassandraTest[ProjectedExtent, SpatialKey, Tile](jConfig, jCredentials) with S3Support with S3Load

object S3IngestTest {
  def apply(implicit jConfig: JConfig, jCredentials: JCredentials, _sc: SparkContext) = new S3IngestTest(jConfig, jCredentials) {
    @transient implicit val sc = _sc
  }
}
