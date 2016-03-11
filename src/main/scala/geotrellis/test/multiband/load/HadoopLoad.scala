package geotrellis.test.multiband.load

import geotrellis.spark.etl.hadoop.MultibandGeoTiffHadoopInput
import geotrellis.test.multiband.SpatialTestEnvironment
import geotrellis.vector.ProjectedExtent

import org.apache.spark.rdd.RDD

trait HadoopLoad { self: SpatialTestEnvironment =>
  val layerName: String = "hadoopIngest"
  val zoom: Int = 8

  def saveToHdfsByteArray =
    saveS3Keys { (path, arr) => writeToHdfs(s"${hadoopLoadPath}${path.split("/").last}", arr) }

  def loadTiles: RDD[(ProjectedExtent, V)] = {
    logger.info("loading tiles from s3 to hdfs...")
    clearLoadPath
    saveToHdfsByteArray
    logger.info("loading tiles from hdfs...")
    val hadoopInput = new MultibandGeoTiffHadoopInput()
    hadoopInput(hadoopParams)
  }
}
