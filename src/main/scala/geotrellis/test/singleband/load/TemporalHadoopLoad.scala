package geotrellis.test.singleband.load

import geotrellis.raster.Tile
import geotrellis.spark.{SpaceTimeKey, TemporalProjectedExtent}
import geotrellis.spark.etl.hadoop.TemporalGeoTiffHadoopInput
import geotrellis.spark.io.hadoop.formats.TemporalGeoTiffInputFormat
import geotrellis.test.TestEnvironment
import org.apache.spark.rdd.RDD

trait TemporalHadoopLoad { self: TestEnvironment[TemporalProjectedExtent, SpaceTimeKey, Tile] =>
  val layerName: String = "hadoopIngest"
  val zoom: Int = 8

  def saveToHdfsByteArray =
    saveS3Keys { (path, arr) => writeToHdfs(s"${hadoopLoadPath}${path.split("/").last}", arr) }

  def loadTiles: RDD[(TemporalProjectedExtent, Tile)] = {
    /*logger.info("loading tiles from s3 to hdfs...")
    clearLoadPath
    saveToHdfsByteArray*/
    logger.info("loading tiles from hdfs...")
    val hadoopInput = new TemporalGeoTiffHadoopInput()
    hadoopInput(hadoopParams)
  }
}
