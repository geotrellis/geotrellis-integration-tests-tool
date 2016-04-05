package geotrellis.test.multiband.load

import geotrellis.raster.MultibandTile
import geotrellis.spark.SpatialKey
import geotrellis.spark.etl.hadoop.MultibandGeoTiffHadoopInput
import geotrellis.test.TestEnvironment
import geotrellis.vector.ProjectedExtent

import org.apache.spark.rdd.RDD

trait HadoopLoad { self: TestEnvironment[ProjectedExtent, SpatialKey, MultibandTile] =>
  def loadTiles: RDD[(ProjectedExtent, MultibandTile)] = {
    logger.info("loading tiles from hdfs...")
    val hadoopInput = new MultibandGeoTiffHadoopInput()
    hadoopInput(loadParams)
  }
}
