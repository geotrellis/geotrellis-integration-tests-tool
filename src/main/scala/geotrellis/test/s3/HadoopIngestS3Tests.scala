package geotrellis.test.s3

import geotrellis.test.hadoop.HadoopLoad
import geotrellis.util.{HadoopSupport, S3Support}
import org.apache.spark.SparkContext

class HadoopIngestS3Tests(@transient implicit val sc: SparkContext) extends S3Tests with HadoopSupport with S3Support with HadoopLoad
