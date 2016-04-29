package geotrellis.test

import geotrellis.config.json.backend.JCredentials
import geotrellis.config.json.dataset.JConfig
import geotrellis.raster.CellGrid
import geotrellis.spark._
import geotrellis.spark.io.cassandra.{CassandraAttributeStore, CassandraLayerReader, CassandraLayerWriter}
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
](jConfig: JConfig, jCredentials: JCredentials) extends TestEnvironment[I, K, V](jConfig, jCredentials) with CassandraSupport {
  @transient lazy val writer = CassandraLayerWriter(instance, table)
  @transient lazy val reader = CassandraLayerReader(instance)
  @transient lazy val attributeStore = CassandraAttributeStore(instance)
}
