package geotrellis.test

import geotrellis.config.Dataset
import geotrellis.raster.CellGrid
import geotrellis.spark._
import geotrellis.spark.io.cassandra._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.util.{CassandraSupport, Component}
import geotrellis.vector.ProjectedExtent

import spray.json.JsonFormat

import scala.reflect.ClassTag

abstract class CassandraTest[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](dataset: Dataset) extends TestEnvironment[I, K, V](dataset) with CassandraSupport {
  @transient lazy val writer         = CassandraLayerWriter(attributeStore, cassandraOutputPath.keyspace, cassandraOutputPath.table)
  @transient lazy val reader         = CassandraLayerReader(attributeStore)
  @transient lazy val deleter        = CassandraLayerDeleter(attributeStore)
  @transient lazy val updater        = CassandraLayerUpdater(attributeStore)
  @transient lazy val tiles          = CassandraValueReader(attributeStore)
  @transient lazy val copier         = CassandraLayerCopier(attributeStore, reader, writer)
  @transient lazy val reindexer      = CassandraLayerReindexer(attributeStore, reader, writer, deleter, copier)
  @transient lazy val mover          = CassandraLayerMover(copier, deleter)
  @transient lazy val attributeStore = CassandraAttributeStore(instance)
}
