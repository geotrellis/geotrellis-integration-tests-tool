package geotrellis.test.s3

import geotrellis.util.S3Support
import org.apache.spark.SparkContext

class TemporalS3IngestS3Tests(@transient implicit val sc: SparkContext) extends TemporalS3Tests with S3Support with TemporalS3Load
