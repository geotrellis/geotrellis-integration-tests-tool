package geotrellis.test.singleband.load

import geotrellis.spark.etl.hadoop.GeoTiffHadoopInput
import geotrellis.test.singleband.SpatialTestEnvironment
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
    val hadoopInput = new GeoTiffHadoopInput()
    hadoopInput(hadoopParams)
  }
}
