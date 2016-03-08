package geotrellis.test.singleband.accumulo

import geotrellis.spark.io._
import geotrellis.spark.io.accumulo._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.singleband.TemporalTestEnvironment
import geotrellis.util.{AccumuloSupport, SparkSupport}

trait TemporalTests extends SparkSupport with TemporalTestEnvironment with AccumuloSupport with Serializable {
  @transient lazy val writer = AccumuloLayerWriter[K, V, M](instance, table, ZCurveKeyIndexMethod.byYear)
  @transient lazy val reader = AccumuloLayerReader[K, V, M](instance)
}
