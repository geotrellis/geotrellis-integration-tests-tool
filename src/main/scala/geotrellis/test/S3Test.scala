package geotrellis.test

import geotrellis.config.json.backend.JCredentials
import geotrellis.config.json.dataset.JConfig
import geotrellis.raster.CellGrid
import geotrellis.spark._
import geotrellis.spark.io.{GenericLayerMover, GenericLayerReindexer}
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.s3._
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.vector.ProjectedExtent
import geotrellis.util.{Component, S3Support}
import spray.json.JsonFormat

import scala.reflect.ClassTag

abstract class S3Test[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](jConfig: JConfig, jCredentials: JCredentials) extends TestEnvironment[I, K, V](jConfig, jCredentials) with S3Support {
  @transient lazy val writer         = S3LayerWriter(attributeStore)
  @transient lazy val reader         = S3LayerReader(attributeStore)
  @transient lazy val copier         = S3LayerCopier(attributeStore, s3OutputPath.bucket, s3OutputPath.prefix)
  @transient lazy val mover          = GenericLayerMover(copier, deleter)
  @transient lazy val reindexer      = GenericLayerReindexer[S3LayerHeader](attributeStore, reader, writer, deleter, copier)
  @transient lazy val deleter        = S3LayerDeleter(attributeStore)
  @transient lazy val updater        = new S3LayerUpdater(attributeStore, reader)
  @transient lazy val attributeStore = S3AttributeStore(s3OutputPath.bucket, s3OutputPath.prefix)
}
