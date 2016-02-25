package geotrellis.test.accumulo

import geotrellis.spark.io.accumulo.{AccumuloLayerReader, AccumuloLayerWriter}
import geotrellis.spark.io.avro.codecs._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.io.json._
import geotrellis.test.TemporalTestEnvironment
import geotrellis.util.{AccumuloSupport, SparkSupport}

trait TemporalAccumuloTests extends SparkSupport with TemporalTestEnvironment with AccumuloSupport with Serializable {
  @transient lazy val writer = AccumuloLayerWriter[K, V, M](instance, table, ZCurveKeyIndexMethod.byYear)
  @transient lazy val reader = AccumuloLayerReader[K, V, M](instance)
}
