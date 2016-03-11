package geotrellis.test.singleband.accumulo

import geotrellis.spark.SpatialKey
import geotrellis.spark.io._
import geotrellis.spark.io.accumulo._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.singleband.SpatialTestEnvironment
import geotrellis.util.AccumuloSupport

abstract class Tests extends SpatialTestEnvironment with AccumuloSupport {
  @transient lazy val writer = AccumuloLayerWriter[SpatialKey, V, M](instance, table, ZCurveKeyIndexMethod)
  @transient lazy val reader = AccumuloLayerReader[SpatialKey, V, M](instance)
  @transient lazy val attributeStore = AccumuloAttributeStore(instance.connector)
}
