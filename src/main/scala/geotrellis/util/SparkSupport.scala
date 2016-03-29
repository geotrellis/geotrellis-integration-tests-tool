package geotrellis.util

import geotrellis.spark.io.hadoop.formats.TemporalGeoTiffInputFormat
import geotrellis.spark.util.SparkUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.{Logger, LoggerFactory}

trait SparkSupport {
  @transient lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

  implicit val sc: SparkContext

  @transient lazy val conf = SparkUtils.hadoopConfiguration
}

object SparkSupport {
  def sparkContext = {
    val context = new SparkContext(
      new SparkConf()
      .setAppName("AccumuloS3Ingest")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryo.registrator", "geotrellis.spark.io.kryo.KryoRegistrator")
      .setJars(SparkContext.jarOfObject(this).toList)
    )

    // probably get that to configuration stuff
    TemporalGeoTiffInputFormat.setTimeTag(context.hadoopConfiguration, "ISO_TIME")
    TemporalGeoTiffInputFormat.setTimeFormat(context.hadoopConfiguration, "yyyy-MM-dd'T'HH:mm:ss")

    context
  }
}
