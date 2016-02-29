package geotrellis.test.hadoop

import geotrellis.test.s3.TemporalS3Load
import geotrellis.util.S3Support
import org.apache.spark.SparkContext

class TemporalS3IngestHadoopTests(@transient implicit val sc: SparkContext) extends TemporalHadoopTests with S3Support with TemporalS3Load
