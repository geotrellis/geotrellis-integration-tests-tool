package geotrellis.test.file

import geotrellis.test.s3.S3Load
import geotrellis.util.S3Support
import org.apache.spark.SparkContext

class S3IngestFileTests(@transient implicit val sc: SparkContext) extends FileTests with S3Support with S3Load
