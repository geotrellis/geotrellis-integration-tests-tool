package geotrellis.test.multiband.hadoop

import geotrellis.test.multiband.TemporalTestEnvironment

import geotrellis.spark.SpaceTimeKey
import geotrellis.spark.io._
import geotrellis.spark.io.hadoop._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.ingest._

import org.apache.hadoop.fs.Path

abstract class TemporalTests extends TemporalTestEnvironment {
  @transient lazy val writer = HadoopLayerWriter[SpaceTimeKey, V, M](new Path(hadoopIngestPath), ZCurveKeyIndexMethod.byYear)
  @transient lazy val reader = HadoopLayerReader[SpaceTimeKey, V, M](new Path(hadoopIngestPath))
  @transient lazy val attributeStore = HadoopAttributeStore.default(hadoopIngestPath)
}
