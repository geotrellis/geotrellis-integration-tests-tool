package geotrellis.test.s3

import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.io.s3._
import geotrellis.test.SpatialTestEnvironment
import geotrellis.util.{S3Support, SparkSupport}

trait S3Tests extends SparkSupport with SpatialTestEnvironment with S3Support with Serializable {
  @transient lazy val writer = S3LayerWriter[K, V, M](s3Bucket, s3IngestPreifx, ZCurveKeyIndexMethod)
  @transient lazy val reader = S3LayerReader[K, V, M](s3Bucket, s3IngestPreifx)
}
