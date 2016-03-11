package geotrellis.test.singleband.file

import geotrellis.spark.SpatialKey
import geotrellis.spark.io._
import geotrellis.spark.io.file._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.singleband.SpatialTestEnvironment
import geotrellis.util.FileSupport

abstract class Tests extends SpatialTestEnvironment with FileSupport {
  @transient lazy val writer = FileLayerWriter[SpatialKey, V, M](fileIngestPath, ZCurveKeyIndexMethod)
  @transient lazy val reader = FileLayerReader[SpatialKey, V, M](fileIngestPath)
  @transient lazy val attributeStore = FileAttributeStore(fileIngestPath)
}
