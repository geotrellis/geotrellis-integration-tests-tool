package geotrellis.test.s3

import geotrellis.spark.io.avro.codecs._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.io.json._
import geotrellis.spark.io.s3.{S3LayerReader, S3LayerWriter}
import geotrellis.test.TemporalTestEnvironment
import geotrellis.util.{S3Support, SparkSupport}

trait TemporalS3Tests extends SparkSupport with TemporalTestEnvironment with S3Support with Serializable {
  @transient lazy val writer = S3LayerWriter[K, V, M](s3Bucket, s3IngestPreifx, ZCurveKeyIndexMethod.byYear)
  @transient lazy val reader = S3LayerReader[K, V, M](s3Bucket, s3IngestPreifx)
}
