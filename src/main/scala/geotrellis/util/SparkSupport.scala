package geotrellis.util

import geotrellis.spark.utils.SparkUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

trait SparkSupport {
  @transient lazy val logger = LoggerFactory.getLogger(this.getClass)

  @transient lazy val _sc: SparkContext = new SparkContext(
    new SparkConf()
      .setAppName("AccumuloS3Ingest")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryo.registrator", "geotrellis.spark.io.kryo.KryoRegistrator")
      .setJars(SparkContext.jarOfObject(this).toList)
  )

  implicit def sc = _sc

  @transient lazy val conf = SparkUtils.hadoopConfiguration

  def scStop = sc.stop
}
