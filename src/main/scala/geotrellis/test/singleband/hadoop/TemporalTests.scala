package geotrellis.test.singleband.hadoop

import geotrellis.spark.io._
import geotrellis.spark.io.hadoop._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.singleband.TemporalTestEnvironment
import geotrellis.util.{HadoopSupport, SparkSupport}
import org.apache.hadoop.fs.Path

trait TemporalTests extends SparkSupport with TemporalTestEnvironment with HadoopSupport with Serializable {
  @transient lazy val writer = HadoopLayerWriter[K, V, M](new Path(hadoopIngestPath), ZCurveKeyIndexMethod.byYear)
  @transient lazy val reader = HadoopLayerReader[K, V, M](new Path(hadoopIngestPath))
  @transient lazy val attributeStore = HadoopAttributeStore.default(hadoopIngestPath)
}
