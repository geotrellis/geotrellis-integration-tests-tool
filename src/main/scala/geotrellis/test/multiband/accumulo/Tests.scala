package geotrellis.test.multiband.accumulo

import geotrellis.spark.SpatialKey
import geotrellis.spark.io._
import geotrellis.spark.io.accumulo._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.multiband.SpatialTestEnvironment
import geotrellis.util.AccumuloSupport

abstract class Tests extends SpatialTestEnvironment with AccumuloSupport {
  @transient lazy val writer = AccumuloLayerWriter(instance, table)
  @transient lazy val reader = AccumuloLayerReader(instance)
  @transient lazy val attributeStore = AccumuloAttributeStore(instance.connector)
}
