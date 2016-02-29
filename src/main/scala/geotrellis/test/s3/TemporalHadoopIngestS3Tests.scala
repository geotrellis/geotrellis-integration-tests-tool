package geotrellis.test.s3

import geotrellis.test.hadoop.TemporalHadoopLoad
import geotrellis.util.{HadoopSupport, S3Support}
import org.apache.spark.SparkContext

class TemporalHadoopIngestS3Tests(@transient implicit val sc: SparkContext) extends TemporalS3Tests with HadoopSupport with S3Support with TemporalHadoopLoad
