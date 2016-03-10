package geotrellis.test.singleband.s3

import geotrellis.spark.io._
import geotrellis.spark.io.s3._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test.singleband.SpatialTestEnvironment
import geotrellis.util.{HadoopSupport, S3Support, SparkSupport}

trait Tests extends SparkSupport with SpatialTestEnvironment with S3Support with HadoopSupport with Serializable {
  @transient lazy val writer = S3LayerWriter[K, V, M](s3Bucket, s3IngestPreifx, ZCurveKeyIndexMethod)
  @transient lazy val reader = S3LayerReader[K, V, M](s3Bucket, s3IngestPreifx)
  @transient lazy val attributeStore = S3AttributeStore(s3Bucket, s3IngestPreifx)
}
