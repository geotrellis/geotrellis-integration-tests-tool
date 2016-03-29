package geotrellis.test

import geotrellis.raster.CellGrid
import geotrellis.spark._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.s3._
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.vector.ProjectedExtent

import spray.json.JsonFormat
import com.typesafe.config.{Config => TConfig}

import scala.reflect.ClassTag

abstract class S3Test[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](configuration: TConfig) extends TestEnvironment[I, K, V](configuration) {
  @transient lazy val writer = S3LayerWriter(s3IngestBucket, s3IngestPrefix)
  @transient lazy val reader = S3LayerReader(s3IngestBucket, s3IngestPrefix)
  @transient lazy val attributeStore = S3AttributeStore(s3IngestBucket, s3IngestPrefix)
}
