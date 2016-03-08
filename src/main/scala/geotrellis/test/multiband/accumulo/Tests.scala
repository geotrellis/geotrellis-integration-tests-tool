package geotrellis.test.multiband.accumulo

import geotrellis.spark.io._
import geotrellis.spark.io.accumulo._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.multiband.SpatialTestEnvironment
import geotrellis.util.{AccumuloSupport, SparkSupport}

trait Tests extends SparkSupport with SpatialTestEnvironment with AccumuloSupport with Serializable {
  @transient lazy val writer = AccumuloLayerWriter[K, V, M](instance, table, ZCurveKeyIndexMethod)
  @transient lazy val reader = AccumuloLayerReader[K, V, M](instance)
}
