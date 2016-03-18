package geotrellis.test.singleband.file

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.file.{FileAttributeStore, FileLayerReader, FileLayerWriter}
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.test.TestEnvironment
import geotrellis.util.FileSupport
import geotrellis.vector.ProjectedExtent
import spray.json.JsonFormat

import scala.reflect.ClassTag

abstract class Tests[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag
] extends TestEnvironment[I, K, Tile] with FileSupport {
  @transient lazy val writer = FileLayerWriter(fileIngestPath)
  @transient lazy val reader = FileLayerReader(fileIngestPath)
  @transient lazy val attributeStore = FileAttributeStore(fileIngestPath)
}
