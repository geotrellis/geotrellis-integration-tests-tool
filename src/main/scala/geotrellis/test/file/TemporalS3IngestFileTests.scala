package geotrellis.test.file

import geotrellis.test.s3.TemporalS3Load
import geotrellis.util.S3Support
import org.apache.spark.SparkContext

class TemporalS3IngestFileTests(@transient implicit val sc: SparkContext) extends TemporalFileTests with S3Support with TemporalS3Load
