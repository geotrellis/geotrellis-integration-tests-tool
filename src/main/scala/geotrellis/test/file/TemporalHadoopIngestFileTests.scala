package geotrellis.test.file

import geotrellis.test.hadoop.TemporalHadoopLoad
import geotrellis.util.{HadoopSupport, S3Support}
import org.apache.spark.SparkContext

class TemporalHadoopIngestFileTests(@transient implicit val sc: SparkContext) extends TemporalFileTests with HadoopSupport with S3Support with TemporalHadoopLoad
