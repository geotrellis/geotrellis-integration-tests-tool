package geotrellis.test.hadoop

import geotrellis.test.s3.S3Load
import geotrellis.util.S3Support
import org.apache.spark.SparkContext

class S3IngestHadoopTests(@transient implicit val sc: SparkContext) extends HadoopTests with S3Support with S3Load
