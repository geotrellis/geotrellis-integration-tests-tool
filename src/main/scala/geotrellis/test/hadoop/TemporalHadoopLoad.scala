package geotrellis.test.hadoop

import geotrellis.spark.etl.hadoop.TemporalGeoTiffHadoopInput
import geotrellis.test.TemporalTestEnvironment
import geotrellis.util.{HadoopSupport, S3Support, SparkSupport}
import org.apache.spark.rdd.RDD

trait TemporalHadoopLoad { self: SparkSupport with TemporalTestEnvironment with HadoopSupport with S3Support =>
  val layerName: String = "hadoopTemporalIngest"
  val zoom: Int = 20

  def saveToHdfsByteArray =
    saveS3Keys { (path, arr) => writeToHdfs(s"${hadoopLoadPath}${path.split("/").last}", arr) }

  def loadTiles: RDD[(I, V)] = {
    logger.info("loading tiles from s3 to hdfs...")
    clearLoadPath
    saveToHdfsByteArray
    logger.info("loading tiles from hdfs...")
    val hadoopInput = new TemporalGeoTiffHadoopInput()
    hadoopInput(hadoopParams)
  }
}
