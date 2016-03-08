package geotrellis.test.singleband.file

import geotrellis.test.singleband.load.TemporalHadoopLoad
import geotrellis.util.{HadoopSupport, S3Support}
import org.apache.spark.SparkContext

class TemporalHadoopIngestTests(@transient implicit val sc: SparkContext) extends TemporalTests with HadoopSupport with S3Support with TemporalHadoopLoad
