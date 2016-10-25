package geotrellis.util

import geotrellis.config.json.dataset.JConfig
import geotrellis.spark.io.hadoop.formats.TemporalGeoTiffInputFormat
import geotrellis.spark.io.kryo.KryoRegistrator
import geotrellis.spark.util.SparkUtils

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.serializer.KryoSerializer

trait SparkSupport {
  implicit val sc: SparkContext

  @transient lazy val conf = SparkUtils.hadoopConfiguration
}

object SparkSupport {
  def sparkContext(timeTag: String = "ISO_TIME", timeFormat: String = "yyyy-MM-dd'T'HH:mm:ss"): SparkContext =
    configureTime(timeTag, timeFormat)(
      new SparkContext(
        new SparkConf()
          .setAppName("GeoTrellis Integration Tests")
          .set("spark.serializer", classOf[KryoSerializer].getName)
          .set("spark.kryo.registrator", classOf[KryoRegistrator].getName)
          .setJars(SparkContext.jarOfObject(this).toList)
      )
    )

  def configureTime(timeTag: String, timeFormat: String)(implicit sc: SparkContext): SparkContext = {
    TemporalGeoTiffInputFormat.setTimeTag(sc.hadoopConfiguration, timeTag)
    TemporalGeoTiffInputFormat.setTimeFormat(sc.hadoopConfiguration, timeFormat)

    sc
  }

  def configureTime(conf: JConfig)(implicit sc: SparkContext): SparkContext = {
    conf.ingestOptions.keyIndexMethod.timeTag.foreach(TemporalGeoTiffInputFormat.setTimeTag(sc.hadoopConfiguration, _))
    conf.ingestOptions.keyIndexMethod.timeFormat.foreach(TemporalGeoTiffInputFormat.setTimeFormat(sc.hadoopConfiguration, _))

    sc
  }
}
