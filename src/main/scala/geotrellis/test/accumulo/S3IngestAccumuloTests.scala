package geotrellis.test.accumulo

import geotrellis.test.s3.S3Load
import geotrellis.util.S3Support
import org.apache.spark.SparkContext

class S3IngestAccumuloTests(@transient implicit val sc: SparkContext) extends AccumuloTests with S3Support with S3Load
