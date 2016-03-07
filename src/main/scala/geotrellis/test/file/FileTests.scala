package geotrellis.test.file

import geotrellis.spark.io._
import geotrellis.spark.io.file._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.SpatialTestEnvironment
import geotrellis.util.{HadoopSupport, SparkSupport}

trait FileTests extends SparkSupport with SpatialTestEnvironment with HadoopSupport with Serializable {
  @transient lazy val writer = FileLayerWriter[K, V, M](hadoopIngestPath, ZCurveKeyIndexMethod)
  @transient lazy val reader = FileLayerReader[K, V, M](hadoopIngestPath)
}
