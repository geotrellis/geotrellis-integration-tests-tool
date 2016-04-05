package geotrellis.test.singleband.load

import geotrellis.raster.Tile
import geotrellis.spark.{SpaceTimeKey, TemporalProjectedExtent}
import geotrellis.spark.etl.hadoop.TemporalGeoTiffHadoopInput
import geotrellis.test.TestEnvironment

import org.apache.spark.rdd.RDD

trait TemporalHadoopLoad { self: TestEnvironment[TemporalProjectedExtent, SpaceTimeKey, Tile] =>
  def loadTiles: RDD[(TemporalProjectedExtent, Tile)] = {
    logger.info("loading tiles from hdfs...")
    val hadoopInput = new TemporalGeoTiffHadoopInput()
    hadoopInput(loadParams)
  }
}
