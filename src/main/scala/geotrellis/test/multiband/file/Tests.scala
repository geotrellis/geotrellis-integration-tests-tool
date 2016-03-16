package geotrellis.test.multiband.file

import geotrellis.spark.SpatialKey
import geotrellis.spark.io._
import geotrellis.spark.io.file._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.multiband.SpatialTestEnvironment
import geotrellis.util.FileSupport

abstract class Tests extends SpatialTestEnvironment with FileSupport {
  @transient lazy val writer = FileLayerWriter(fileIngestPath)
  @transient lazy val reader = FileLayerReader(fileIngestPath)
  @transient lazy val attributeStore = FileAttributeStore(fileIngestPath)
}
