package geotrellis.util

import geotrellis.spark.util.SparkUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

trait SparkSupport {
  @transient lazy val logger = LoggerFactory.getLogger(this.getClass)

  implicit val sc: SparkContext

  @transient lazy val conf = SparkUtils.hadoopConfiguration
}

object SparkSupport {
  def sparkContext = new SparkContext(
    new SparkConf()
      .setAppName("AccumuloS3Ingest")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryo.registrator", "geotrellis.spark.io.kryo.KryoRegistrator")
      .setJars(SparkContext.jarOfObject(this).toList)
  )
}
