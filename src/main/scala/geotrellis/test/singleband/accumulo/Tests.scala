package geotrellis.test.singleband.accumulo

import geotrellis.spark.io._
import geotrellis.spark.io.accumulo._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.singleband.SpatialTestEnvironment
import geotrellis.util.{HadoopSupport, AccumuloSupport, SparkSupport}

trait Tests extends SparkSupport with SpatialTestEnvironment with AccumuloSupport with HadoopSupport with Serializable {
  @transient lazy val writer = AccumuloLayerWriter[K, V, M](instance, table, ZCurveKeyIndexMethod)
  @transient lazy val reader = AccumuloLayerReader[K, V, M](instance)
  @transient lazy val attributeStore = AccumuloAttributeStore(instance.connector)
}
