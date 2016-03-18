package geotrellis.test.multiband.accumulo

import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.accumulo._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.test.TestEnvironment
import geotrellis.util.AccumuloSupport
import geotrellis.vector.ProjectedExtent
import spray.json.JsonFormat

import scala.reflect.ClassTag

abstract class Tests[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag
] extends TestEnvironment[I, K, MultibandTile] with AccumuloSupport {
  @transient lazy val writer = AccumuloLayerWriter(instance, table)
  @transient lazy val reader = AccumuloLayerReader(instance)
  @transient lazy val attributeStore = AccumuloAttributeStore(instance.connector)
}
