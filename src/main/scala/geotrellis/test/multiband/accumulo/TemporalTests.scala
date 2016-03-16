package geotrellis.test.multiband.accumulo

import geotrellis.spark.SpaceTimeKey
import geotrellis.spark.io._
import geotrellis.spark.io.accumulo._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.multiband.TemporalTestEnvironment
import geotrellis.util.AccumuloSupport

abstract class TemporalTests extends TemporalTestEnvironment with AccumuloSupport {
  @transient lazy val writer = AccumuloLayerWriter(instance, table)
  @transient lazy val reader = AccumuloLayerReader(instance)
  @transient lazy val attributeStore = AccumuloAttributeStore(instance.connector)
}
