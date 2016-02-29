package geotrellis.test.s3

import geotrellis.util.S3Support
import org.apache.spark.SparkContext

class S3IngestS3Tests(@transient implicit val sc: SparkContext) extends S3Tests with S3Support with S3Load
