package geotrellis.test.multiband.hadoop

import geotrellis.test.multiband.SpatialTestEnvironment

import geotrellis.spark.SpatialKey
import geotrellis.spark.io._
import geotrellis.spark.io.hadoop._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.ingest._

import org.apache.hadoop.fs.Path

abstract class Tests extends SpatialTestEnvironment {
  @transient lazy val writer = HadoopLayerWriter(new Path(hadoopIngestPath))
  @transient lazy val reader = HadoopLayerReader(new Path(hadoopIngestPath))
  @transient lazy val attributeStore = HadoopAttributeStore(hadoopIngestPath)
}
