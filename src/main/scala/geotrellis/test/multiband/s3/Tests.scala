package geotrellis.test.multiband.s3

import geotrellis.test.multiband.SpatialTestEnvironment

import geotrellis.spark.SpatialKey
import geotrellis.spark.io._
import geotrellis.spark.io.s3._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.ingest._

abstract class Tests extends SpatialTestEnvironment {
  @transient lazy val writer = S3LayerWriter(s3Bucket, s3IngestPreifx)
  @transient lazy val reader = S3LayerReader(s3Bucket, s3IngestPreifx)
  @transient lazy val attributeStore = S3AttributeStore(s3Bucket, s3IngestPreifx)
}
