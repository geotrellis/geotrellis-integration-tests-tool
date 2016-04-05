package geotrellis.test

import geotrellis.config.json.backend.JCredensials
import geotrellis.config.json.dataset.JConfig
import geotrellis.raster.CellGrid
import geotrellis.spark._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.file.{FileAttributeStore, FileLayerReader, FileLayerWriter}
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.vector.ProjectedExtent
import geotrellis.util.{Component, HadoopSupport}

import spray.json.JsonFormat

import scala.reflect.ClassTag

abstract class FileTest[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](jConfig: JConfig, jCredensials: JCredensials) extends TestEnvironment[I, K, V](jConfig, jCredensials) with HadoopSupport {
  @transient lazy val writer = FileLayerWriter(hadoopIngestPath)
  @transient lazy val reader = FileLayerReader(hadoopIngestPath)
  @transient lazy val attributeStore = FileAttributeStore(hadoopIngestPath)
}
