package geotrellis.test.multiband.file

import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.FileTest
import geotrellis.test.multiband.load.S3Load
import geotrellis.vector.ProjectedExtent
import org.apache.spark.SparkContext
import com.typesafe.config.{Config => TConfig}
import geotrellis.util.S3Support

abstract class S3IngestTest(implicit configuration: TConfig) extends FileTest[ProjectedExtent, SpatialKey, MultibandTile](configuration) with S3Support with S3Load

object S3IngestTest {
  def apply(implicit configuration: TConfig, _sc: SparkContext) = new S3IngestTest {
    @transient implicit val sc = _sc
  }
}
