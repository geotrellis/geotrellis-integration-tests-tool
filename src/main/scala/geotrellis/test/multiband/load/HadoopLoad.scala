package geotrellis.test.multiband.load

import geotrellis.spark.etl.hadoop.MultibandGeoTiffHadoopInput
import geotrellis.test.multiband.SpatialTestEnvironment
import geotrellis.util.{HadoopSupport, S3Support, SparkSupport}
import org.apache.spark.rdd.RDD

trait HadoopLoad { self: SparkSupport with SpatialTestEnvironment with HadoopSupport with S3Support  =>
  val layerName: String = "hadoopIngest"
  val zoom: Int = 7

  def saveToHdfsByteArray =
    saveS3Keys { (path, arr) => writeToHdfs(s"${hadoopLoadPath}${path.split("/").last}", arr) }

  def loadTiles: RDD[(I, V)] = {
    logger.info("loading tiles from s3 to hdfs...")
    clearLoadPath
    saveToHdfsByteArray
    logger.info("loading tiles from hdfs...")
    val hadoopInput = new MultibandGeoTiffHadoopInput()
    hadoopInput(hadoopParams)
  }
}
