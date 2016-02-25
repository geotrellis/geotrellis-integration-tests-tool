package geotrellis.test.hadoop

import geotrellis.spark.io.hadoop.{HadoopLayerReader, HadoopLayerWriter}
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.io.json._
import geotrellis.test.TemporalTestEnvironment
import geotrellis.util.{HadoopSupport, SparkSupport}
import org.apache.hadoop.fs.Path

trait TemporalHadoopTests extends SparkSupport with TemporalTestEnvironment with HadoopSupport with Serializable {
  @transient lazy val writer = HadoopLayerWriter[K, V, M](new Path(hadoopIngestPath), ZCurveKeyIndexMethod.byYear)
  @transient lazy val reader = HadoopLayerReader[K, V, M](new Path(hadoopIngestPath))
}
