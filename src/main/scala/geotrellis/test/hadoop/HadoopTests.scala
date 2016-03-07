package geotrellis.test.hadoop

import geotrellis.spark.io._
import geotrellis.spark.io.hadoop._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.SpatialTestEnvironment
import geotrellis.util.{HadoopSupport, SparkSupport}
import org.apache.hadoop.fs.Path

trait HadoopTests extends SparkSupport with SpatialTestEnvironment with HadoopSupport with Serializable {
  @transient lazy val writer = HadoopLayerWriter[K, V, M](new Path(hadoopIngestPath), ZCurveKeyIndexMethod)
  @transient lazy val reader = HadoopLayerReader[K, V, M](new Path(hadoopIngestPath))
}
