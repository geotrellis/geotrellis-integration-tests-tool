package geotrellis.test.multiband.file

import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.FileTest
import geotrellis.test.multiband.load.HadoopLoad
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext
import com.typesafe.config.{Config => TConfig}

abstract class HadoopIngestTest(implicit configuration: TConfig) extends FileTest[ProjectedExtent, SpatialKey, MultibandTile](configuration) with HadoopLoad

object HadoopIngestTest {
  def apply(implicit configuration: TConfig, _sc: SparkContext) = new HadoopIngestTest {
    @transient implicit val sc = _sc
  }
}
