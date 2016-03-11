package geotrellis.test.multiband.load

/*import geotrellis.spark.TemporalProjectedExtent
import geotrellis.spark.etl.hadoop.TemporalMultibandGeoTiffHadoopInput
import geotrellis.test.multiband.TemporalTestEnvironment

import org.apache.spark.rdd.RDD

trait TemporalHadoopLoad { self: TemporalTestEnvironment =>
  val layerName: String = "hadoopIngest"
  val zoom: Int = 8

  def saveToHdfsByteArray =
    saveS3Keys { (path, arr) => writeToHdfs(s"${hadoopLoadPath}${path.split("/").last}", arr) }

  def loadTiles: RDD[(TemporalProjectedExtent, V)] = {
    logger.info("loading tiles from s3 to hdfs...")
    clearLoadPath
    saveToHdfsByteArray
    logger.info("loading tiles from hdfs...")
    val hadoopInput = new TemporalMultibandGeoTiffHadoopInput()
    hadoopInput(hadoopParams)
  }
}*/
