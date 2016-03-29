package geotrellis.test.singleband.hadoop

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test.HadoopTest
import geotrellis.test.singleband.load.HadoopLoad
import geotrellis.vector.ProjectedExtent

import org.apache.spark.SparkContext
import com.typesafe.config.{Config => TConfig}

abstract class HadoopIngestTest(implicit configuration: TConfig) extends HadoopTest[ProjectedExtent, SpatialKey, Tile](configuration) with HadoopLoad

object HadoopIngestTest {
  def apply(implicit configuration: TConfig, _sc: SparkContext) = new HadoopIngestTest {
    @transient implicit val sc = _sc
  }
}
