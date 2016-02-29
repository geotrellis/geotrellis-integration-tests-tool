package geotrellis.test.file

import geotrellis.spark.io.file.{FileLayerReader, FileLayerWriter}
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.io.json._
import geotrellis.test.SpatialTestEnvironment
import geotrellis.util.{HadoopSupport, SparkSupport}
import geotrellis.spark.io.avro.codecs._

trait FileTests extends SparkSupport with SpatialTestEnvironment with HadoopSupport with Serializable {
  @transient lazy val writer = FileLayerWriter[K, V, M](hadoopIngestPath, ZCurveKeyIndexMethod)
  @transient lazy val reader = FileLayerReader[K, V, M](hadoopIngestPath)
}
