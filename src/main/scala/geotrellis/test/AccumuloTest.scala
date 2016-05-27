package geotrellis.test

import geotrellis.config.json.backend.JCredentials
import geotrellis.config.json.dataset.JConfig
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
](jConfig: JConfig, jCredentials: JCredentials) extends TestEnvironment[I, K, V](jConfig, jCredentials) with AccumuloSupport {
  @transient lazy val writer         = AccumuloLayerWriter(instance, table)
  @transient lazy val reader         = AccumuloLayerReader(instance)
  @transient lazy val copier         = AccumuloLayerCopier(instance)
  @transient lazy val mover          = AccumuloLayerMover(instance)
  @transient lazy val reindexer      = AccumuloLayerReindexer(instance)
  @transient lazy val deleter        = AccumuloLayerDeleter(instance)
  @transient lazy val updater        = AccumuloLayerUpdater(instance)
  @transient lazy val attributeStore = AccumuloAttributeStore(instance.connector)
}
