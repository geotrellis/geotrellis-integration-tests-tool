package geotrellis.test.singleband.s3

import geotrellis.test.singleband.TemporalTestEnvironment

import geotrellis.spark.SpaceTimeKey
import geotrellis.spark.io._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.io.s3._
import geotrellis.spark.ingest._

abstract class TemporalTests extends TemporalTestEnvironment {
  @transient lazy val writer = S3LayerWriter[SpaceTimeKey, V, M](s3Bucket, s3IngestPreifx, ZCurveKeyIndexMethod.byYear)
  @transient lazy val reader = S3LayerReader[SpaceTimeKey, V, M](s3Bucket, s3IngestPreifx)
  @transient lazy val attributeStore = S3AttributeStore(s3Bucket, s3IngestPreifx)
}
