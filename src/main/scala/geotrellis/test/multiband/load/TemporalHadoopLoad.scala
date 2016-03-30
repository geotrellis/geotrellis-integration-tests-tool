package geotrellis.test.multiband.load

import geotrellis.raster.MultibandTile
import geotrellis.spark.{SpaceTimeKey, TemporalProjectedExtent}
import geotrellis.spark.etl.hadoop.TemporalMultibandGeoTiffHadoopInput
import geotrellis.test.TestEnvironment

import org.apache.spark.rdd.RDD

trait TemporalHadoopLoad { self: TestEnvironment[TemporalProjectedExtent, SpaceTimeKey, MultibandTile] =>
  val layerName: String = "hadoopIngest"
  val zoom: Int = 8

  def loadTiles: RDD[(TemporalProjectedExtent, MultibandTile)] = {
    logger.info("loading tiles from hdfs...")
    val hadoopInput = new TemporalMultibandGeoTiffHadoopInput()
    hadoopInput(loadParams)
  }
}
