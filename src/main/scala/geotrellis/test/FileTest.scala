package geotrellis.test

import geotrellis.config.json.backend.JCredentials
import geotrellis.config.json.dataset.JConfig
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
](jConfig: JConfig, jCredentials: JCredentials) extends TestEnvironment[I, K, V](jConfig, jCredentials) with HadoopSupport {
  @transient lazy val writer         = FileLayerWriter(hadoopIngestPath)
  @transient lazy val reader         = FileLayerReader(hadoopIngestPath)
  @transient lazy val copier         = FileLayerCopier(hadoopIngestPath)
  @transient lazy val mover          = FileLayerMover(hadoopIngestPath)
  @transient lazy val reindexer      = FileLayerReindexer(hadoopIngestPath)
  @transient lazy val deleter        = FileLayerDeleter(hadoopIngestPath)
  @transient lazy val updater        = FileLayerUpdater(hadoopIngestPath)
  @transient lazy val attributeStore = FileAttributeStore(hadoopIngestPath)
}
