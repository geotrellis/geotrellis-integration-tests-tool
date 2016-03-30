package geotrellis

import org.apache.spark.SparkContext
import com.typesafe.config.{Config => TConfig}

import scala.util.matching.Regex

package object test {
  private val idRx = "[A-Z0-9]{20}"
  private val keyRx = "[a-zA-Z0-9+/]+={0,2}"
  private val slug = "[a-zA-Z0-9-]+"
  val S3UrlRx = new Regex(s"""s3n://(?:($idRx):($keyRx)@)?($slug)/{0,1}(.*)""", "aws_id", "aws_key", "bucket", "prefix")

  def getInputParams(path: String): Map[String, String] = {
    // s3 case
    if(path.contains("s3n://")) {
      val S3UrlRx(_, _, bucket, prefix) = path
      Map("bucket" -> bucket, "key" -> prefix)
    } else if (path.contains("hdfs://")) Map("path" -> path.split("hdfs://")(1)) // hadoop case
    else Map("path" -> path) // file / accumulo cases
  }

  def s3Tests(implicit configuration: TConfig, sc: SparkContext) = singleband.s3Tests ++ multiband.s3Tests
  def hadoopTests(implicit configuration: TConfig, sc: SparkContext) = singleband.hadoopTests ++ multiband.hadoopTests

  def s3TestsTemporal(implicit configuration: TConfig, sc: SparkContext) = singleband.s3TestsTemporal ++ multiband.s3TestsTemporal
  def hadoopTestsTemporal(implicit configuration: TConfig, sc: SparkContext) = singleband.hadoopTestsTemporal ++ multiband.hadoopTestsTemporal
}
