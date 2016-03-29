package geotrellis.test

import geotrellis.raster.CellGrid
import geotrellis.spark._
import geotrellis.spark.io.accumulo.{AccumuloAttributeStore, AccumuloLayerReader, AccumuloLayerWriter}
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.util.AccumuloSupport
import geotrellis.vector.ProjectedExtent
import geotrellis.config._

import spray.json.JsonFormat
import com.typesafe.config.{Config => TConfig}

import scala.reflect.ClassTag

abstract class AccumuloTest[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](configuration: TConfig) extends TestEnvironment[I, K, V](configuration) with AccumuloSupport {
  lazy val table = either("ingestPath", "gtintegration")(configuration)
  @transient lazy val writer = AccumuloLayerWriter(instance, table)
  @transient lazy val reader = AccumuloLayerReader(instance)
  @transient lazy val attributeStore = AccumuloAttributeStore(instance.connector)
}
