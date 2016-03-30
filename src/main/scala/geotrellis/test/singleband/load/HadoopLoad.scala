package geotrellis.test.singleband.load

import geotrellis.raster.Tile
import geotrellis.spark.SpatialKey
import geotrellis.spark.etl.hadoop.GeoTiffHadoopInput
import geotrellis.test.TestEnvironment
import geotrellis.vector.ProjectedExtent

import org.apache.spark.rdd.RDD

trait HadoopLoad { self: TestEnvironment[ProjectedExtent, SpatialKey, Tile] =>
  val layerName: String = "hadoopIngest"
  val zoom: Int = 8

  def loadTiles: RDD[(ProjectedExtent, Tile)] = {
    logger.info("loading tiles from hdfs...")
    val hadoopInput = new GeoTiffHadoopInput()
    hadoopInput(loadParams)
  }
}
