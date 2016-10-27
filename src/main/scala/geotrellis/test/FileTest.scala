package geotrellis.test

import geotrellis.config.Dataset
import geotrellis.raster.CellGrid
import geotrellis.spark._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.file._
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.vector.ProjectedExtent
import geotrellis.util.{Component, HadoopSupport}

import spray.json.JsonFormat

import scala.reflect.ClassTag

abstract class FileTest[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](dataset: Dataset) extends TestEnvironment[I, K, V](dataset) with HadoopSupport {
  @transient lazy val writer         = FileLayerWriter(hadoopOutputPath.path)
  @transient lazy val reader         = FileLayerReader(hadoopOutputPath.path)
  @transient lazy val copier         = FileLayerCopier(hadoopOutputPath.path)
  @transient lazy val mover          = FileLayerMover(hadoopOutputPath.path)
  @transient lazy val reindexer      = FileLayerReindexer(hadoopOutputPath.path)
  @transient lazy val deleter        = FileLayerDeleter(hadoopOutputPath.path)
  @transient lazy val updater        = FileLayerUpdater(hadoopOutputPath.path)
  @transient lazy val attributeStore = FileAttributeStore(hadoopOutputPath.path)
}
