package geotrellis.test.accumulo

import geotrellis.spark.io.accumulo.{AccumuloLayerReader, AccumuloLayerWriter}
import geotrellis.spark.io.avro.codecs._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.io.json._
import geotrellis.test.TestEnvironment
import geotrellis.util.{AccumuloSupport, SparkSupport}

trait AccumuloTests extends SparkSupport with TestEnvironment with AccumuloSupport with Serializable {
  lazy val writer = AccumuloLayerWriter[K, V, M](instance, table, ZCurveKeyIndexMethod)
  lazy val reader = AccumuloLayerReader[K, V, M](instance)
}
