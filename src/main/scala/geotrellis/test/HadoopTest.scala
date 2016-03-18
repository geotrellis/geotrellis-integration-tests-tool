package geotrellis.test

import geotrellis.raster.{CellGrid, MultibandTile}
import geotrellis.spark._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.hadoop._
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.util.FileSupport
import geotrellis.vector.ProjectedExtent
import org.apache.hadoop.fs.Path
import spray.json.JsonFormat

import scala.reflect.ClassTag

abstract class HadoopTest[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
] extends TestEnvironment[I, K, V] with FileSupport {
  @transient lazy val writer = HadoopLayerWriter(new Path(hadoopIngestPath))
  @transient lazy val reader = HadoopLayerReader(new Path(hadoopIngestPath))
  @transient lazy val attributeStore = HadoopAttributeStore(hadoopIngestPath)
}
