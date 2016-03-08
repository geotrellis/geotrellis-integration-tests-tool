package geotrellis.test.multiband.hadoop

import geotrellis.spark.io._
import geotrellis.spark.io.hadoop._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.multiband.SpatialTestEnvironment
import geotrellis.util.{HadoopSupport, SparkSupport}
import org.apache.hadoop.fs.Path

trait Tests extends SparkSupport with SpatialTestEnvironment with HadoopSupport with Serializable {
  @transient lazy val writer = HadoopLayerWriter[K, V, M](new Path(hadoopIngestPath), ZCurveKeyIndexMethod)
  @transient lazy val reader = HadoopLayerReader[K, V, M](new Path(hadoopIngestPath))
}
