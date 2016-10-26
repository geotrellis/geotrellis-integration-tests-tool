package geotrellis.test

import geotrellis.config.Dataset
import geotrellis.raster.CellGrid
import geotrellis.spark._
import geotrellis.spark.io.hbase._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.util.{HBaseSupport, Component}
import geotrellis.vector.ProjectedExtent
import spray.json.JsonFormat

import scala.reflect.ClassTag

abstract class HBaseTest[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](dataset: Dataset) extends TestEnvironment[I, K, V](dataset) with HBaseSupport {
  @transient lazy val writer         = HBaseLayerWriter(attributeStore, hbaseOutputPath.table)
  @transient lazy val reader         = HBaseLayerReader(attributeStore)
  @transient lazy val copier         = HBaseLayerCopier(instance)
  @transient lazy val mover          = HBaseLayerMover(instance)
  @transient lazy val reindexer      = HBaseLayerReindexer(attributeStore)
  @transient lazy val deleter        = HBaseLayerDeleter(attributeStore)
  @transient lazy val updater        = HBaseLayerUpdater(attributeStore)
  @transient lazy val attributeStore = HBaseAttributeStore(instance)
}
