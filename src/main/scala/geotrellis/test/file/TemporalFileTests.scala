package geotrellis.test.file

import geotrellis.spark.io.file.{FileLayerReader, FileLayerWriter}
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.io.json._
import geotrellis.test.TemporalTestEnvironment
import geotrellis.util.{HadoopSupport, SparkSupport}
import geotrellis.spark.io.avro.codecs._

trait TemporalFileTests extends SparkSupport with TemporalTestEnvironment with HadoopSupport with Serializable {
  @transient lazy val writer = FileLayerWriter[K, V, M](hadoopIngestPath, ZCurveKeyIndexMethod.byYear)
  @transient lazy val reader = FileLayerReader[K, V, M](hadoopIngestPath)
}
