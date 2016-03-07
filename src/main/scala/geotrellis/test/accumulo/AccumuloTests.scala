package geotrellis.test.accumulo

import geotrellis.spark.io._
import geotrellis.spark.io.accumulo._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.SpatialTestEnvironment
import geotrellis.util.{AccumuloSupport, SparkSupport}

trait AccumuloTests extends SparkSupport with SpatialTestEnvironment with AccumuloSupport with Serializable {
  @transient lazy val writer = AccumuloLayerWriter[K, V, M](instance, table, ZCurveKeyIndexMethod)
  @transient lazy val reader = AccumuloLayerReader[K, V, M](instance)
}
