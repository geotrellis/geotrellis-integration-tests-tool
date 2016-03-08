package geotrellis.test.multiband.file

import geotrellis.spark.io._
import geotrellis.spark.io.file._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.multiband.TemporalTestEnvironment
import geotrellis.util.{HadoopSupport, SparkSupport}

trait TemporalTests extends SparkSupport with TemporalTestEnvironment with HadoopSupport with Serializable {
  @transient lazy val writer = FileLayerWriter[K, V, M](hadoopIngestPath, ZCurveKeyIndexMethod.byYear)
  @transient lazy val reader = FileLayerReader[K, V, M](hadoopIngestPath)
}
