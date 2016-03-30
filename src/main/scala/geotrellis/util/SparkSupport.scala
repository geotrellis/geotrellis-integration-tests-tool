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
  def sparkContext(timeTag: String = "ISO_TIME", timeFormat: String = "yyyy-MM-dd'T'HH:mm:ss") = {
    val context = new SparkContext(
      new SparkConf()
      .setAppName("GeoTrellis Integration Tests")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryo.registrator", "geotrellis.spark.io.kryo.KryoRegistrator")
      .setJars(SparkContext.jarOfObject(this).toList)
    )

    TemporalGeoTiffInputFormat.setTimeTag(context.hadoopConfiguration, timeTag)
    TemporalGeoTiffInputFormat.setTimeFormat(context.hadoopConfiguration, timeFormat)

    context
  }
}
