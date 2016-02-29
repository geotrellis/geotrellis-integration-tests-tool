package geotrellis.test.hadoop

import geotrellis.util.{HadoopSupport, S3Support}
import org.apache.spark.SparkContext

class HadoopIngestHadoopTests(@transient implicit val sc: SparkContext) extends HadoopTests with HadoopSupport with S3Support with HadoopLoad
