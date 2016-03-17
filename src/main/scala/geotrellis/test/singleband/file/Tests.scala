package geotrellis.test.singleband.file

import geotrellis.spark.io.file._
import geotrellis.test.singleband.SpatialTestEnvironment
import geotrellis.util.FileSupport

abstract class Tests extends SpatialTestEnvironment with FileSupport {
  @transient lazy val writer = FileLayerWriter(fileIngestPath)
  @transient lazy val reader = FileLayerReader(fileIngestPath)
  @transient lazy val attributeStore = FileAttributeStore(fileIngestPath)
}
