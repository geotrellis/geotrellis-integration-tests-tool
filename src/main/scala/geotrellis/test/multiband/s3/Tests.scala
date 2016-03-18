package geotrellis.test.multiband.s3

import geotrellis.raster.MultibandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.s3._
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.test.TestEnvironment
import geotrellis.util.FileSupport
import geotrellis.vector.ProjectedExtent
import spray.json.JsonFormat

import scala.reflect.ClassTag

abstract class Tests[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag
] extends TestEnvironment[I, K, MultibandTile] with FileSupport {
  @transient lazy val writer = S3LayerWriter(s3Bucket, s3IngestPreifx)
  @transient lazy val reader = S3LayerReader(s3Bucket, s3IngestPreifx)
  @transient lazy val attributeStore = S3AttributeStore(s3Bucket, s3IngestPreifx)
}
