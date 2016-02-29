package geotrellis.test.accumulo

import geotrellis.test.s3.TemporalS3Load
import geotrellis.util.S3Support
import org.apache.spark.SparkContext

class TemporalS3IngestAccumuloTests(@transient implicit val sc: SparkContext) extends TemporalAccumuloTests with S3Support with TemporalS3Load
