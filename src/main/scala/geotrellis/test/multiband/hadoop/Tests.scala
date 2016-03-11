package geotrellis.test.multiband.hadoop

import geotrellis.test.multiband.SpatialTestEnvironment

import geotrellis.spark.SpatialKey
import geotrellis.spark.io._
import geotrellis.spark.io.hadoop._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.ingest._

import org.apache.hadoop.fs.Path

abstract class Tests extends SpatialTestEnvironment {
  @transient lazy val writer = HadoopLayerWriter[SpatialKey, V, M](new Path(hadoopIngestPath), ZCurveKeyIndexMethod)
  @transient lazy val reader = HadoopLayerReader[SpatialKey, V, M](new Path(hadoopIngestPath))
  @transient lazy val attributeStore = HadoopAttributeStore.default(hadoopIngestPath)
}
