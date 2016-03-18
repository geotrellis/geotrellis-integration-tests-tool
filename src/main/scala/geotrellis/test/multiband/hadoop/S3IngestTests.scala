package geotrellis.test.multiband.hadoop

import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.multiband.load.S3Load
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext

abstract class S3IngestTests extends Tests[ProjectedExtent, SpatialKey] with S3Load

object S3IngestTests {
  def apply(implicit _sc: SparkContext) = new S3IngestTests {
    @transient implicit val sc = _sc
  }
}
