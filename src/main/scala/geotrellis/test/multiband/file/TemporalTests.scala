package geotrellis.test.multiband.file

import geotrellis.spark.SpaceTimeKey
import geotrellis.spark.io._
import geotrellis.spark.io.file._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.multiband.TemporalTestEnvironment
import geotrellis.util.FileSupport

abstract class TemporalTests extends TemporalTestEnvironment with FileSupport {
  @transient lazy val writer = FileLayerWriter[SpaceTimeKey, V, M](fileIngestPath, ZCurveKeyIndexMethod.byYear)
  @transient lazy val reader = FileLayerReader[SpaceTimeKey, V, M](fileIngestPath)
  @transient lazy val attributeStore = FileAttributeStore(fileIngestPath)
}
