package geotrellis.test

import geotrellis.raster.CellGrid
import geotrellis.spark._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.s3._
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.vector.ProjectedExtent
import geotrellis.config.DataSet
import geotrellis.util.S3Support

import spray.json.JsonFormat

import scala.reflect.ClassTag

abstract class S3Test[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](dataSet: DataSet) extends TestEnvironment[I, K, V](dataSet) with S3Support {
  @transient lazy val writer = S3LayerWriter(s3IngestBucket, s3IngestPrefix)
  @transient lazy val reader = S3LayerReader(s3IngestBucket, s3IngestPrefix)
  @transient lazy val attributeStore = S3AttributeStore(s3IngestBucket, s3IngestPrefix)
}
