package geotrellis.test.multiband.accumulo

import geotrellis.spark.io._
import geotrellis.spark.io.accumulo._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.multiband.TemporalTestEnvironment
import geotrellis.util.{HadoopSupport, AccumuloSupport, SparkSupport}

trait TemporalTests extends SparkSupport with TemporalTestEnvironment with AccumuloSupport with HadoopSupport with Serializable {
  @transient lazy val writer = AccumuloLayerWriter[K, V, M](instance, table, ZCurveKeyIndexMethod.byYear)
  @transient lazy val reader = AccumuloLayerReader[K, V, M](instance)
  @transient lazy val attributeStore = AccumuloAttributeStore(instance.connector)
}
