package geotrellis.test.singleband.file

import geotrellis.spark.io._
import geotrellis.spark.io.file._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.singleband.TemporalTestEnvironment
import geotrellis.util.{FileSupport, HadoopSupport, SparkSupport}

trait TemporalTests extends SparkSupport with TemporalTestEnvironment with HadoopSupport with FileSupport with Serializable {
  @transient lazy val writer = FileLayerWriter[K, V, M](fileIngestPath, ZCurveKeyIndexMethod.byYear)
  @transient lazy val reader = FileLayerReader[K, V, M](fileIngestPath)
  @transient lazy val attributeStore = FileAttributeStore(fileIngestPath)
}
