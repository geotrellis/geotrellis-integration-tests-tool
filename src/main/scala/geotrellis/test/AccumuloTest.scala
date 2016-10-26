package geotrellis.test

import geotrellis.config.Dataset
import geotrellis.raster.CellGrid
import geotrellis.spark._
import geotrellis.spark.io.accumulo._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.util.{AccumuloSupport, Component}
import geotrellis.vector.ProjectedExtent

import spray.json.JsonFormat

import scala.reflect.ClassTag

abstract class AccumuloTest[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](dataset: Dataset) extends TestEnvironment[I, K, V](dataset) with AccumuloSupport {
  @transient lazy val writer         = AccumuloLayerWriter(instance, accumuloOutputPath.table, SocketWriteStrategy())
  @transient lazy val reader         = AccumuloLayerReader(instance)
  @transient lazy val copier         = AccumuloLayerCopier(instance, SocketWriteStrategy())
  @transient lazy val mover          = AccumuloLayerMover(instance, SocketWriteStrategy())
  @transient lazy val reindexer      = AccumuloLayerReindexer(instance, SocketWriteStrategy())
  @transient lazy val deleter        = AccumuloLayerDeleter(instance)
  @transient lazy val updater        = AccumuloLayerUpdater(instance, SocketWriteStrategy())
  @transient lazy val attributeStore = AccumuloAttributeStore(instance.connector)
}
