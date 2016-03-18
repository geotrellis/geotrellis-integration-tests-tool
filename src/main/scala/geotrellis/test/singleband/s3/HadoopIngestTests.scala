package geotrellis.test.singleband.s3

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.S3Tests
import geotrellis.test.singleband.load.HadoopLoad
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext

abstract class HadoopIngestTests extends S3Tests[ProjectedExtent, SpatialKey, Tile] with HadoopLoad

object HadoopIngestTests {
  def apply(implicit _sc: SparkContext) = new HadoopIngestTests {
    @transient implicit val sc = _sc
  }
}
