package geotrellis.test.singleband.cassandra

import geotrellis.config.json.backend.JCredentials
import geotrellis.config.json.dataset.JConfig
import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.CassandraTest
import geotrellis.test.singleband.load.HadoopLoad
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext

abstract class HadoopIngestTest(jConfig: JConfig, jCredentials: JCredentials) extends CassandraTest[ProjectedExtent, SpatialKey, Tile](jConfig, jCredentials) with HadoopLoad

object HadoopIngestTest {
  def apply(implicit jConfig: JConfig, jCredentials: JCredentials, _sc: SparkContext) = new HadoopIngestTest(jConfig, jCredentials) {
    @transient implicit val sc = _sc
  }
}
