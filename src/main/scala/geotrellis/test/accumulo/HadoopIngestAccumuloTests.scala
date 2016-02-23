package geotrellis.test.accumulo

import geotrellis.spark.LayerId
import geotrellis.spark.etl.hadoop.GeoTiffHadoopInput
import geotrellis.util.{HadoopSupport, S3Support}

import org.apache.spark.rdd.RDD

class HadoopIngestAccumuloTests extends AccumuloTests with HadoopSupport with S3Support {
  val layerName: String = "hadoopingest-accumulo"
  val zoom: Int = 20

  def saveToHdfsByteArray =
    saveS3Keys { (path, arr) => writeToHdfs(s"${hadoopLoadPath}${path.split("/").last}", arr) }

  def loadTiles: RDD[(I, V)] = {
    logger.info("loading tiles from s3 to hdfs...")
    clearLoadPath
    saveToHdfsByteArray
    logger.info("loading tiles from hdfs...")
    val hadoopInput = new GeoTiffHadoopInput()
    hadoopInput(hadoopParams)
  }

  def spatialIngest: Unit = spatialIngest(layerName)
  def combineLayers: Unit = combineLayers(LayerId(layerName, zoom))
}
