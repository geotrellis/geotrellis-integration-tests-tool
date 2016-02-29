package geotrellis.test.accumulo

import geotrellis.test.hadoop.TemporalHadoopLoad
import geotrellis.util.{HadoopSupport, S3Support}
import org.apache.spark.SparkContext

class TemporalHadoopIngestAccumuloTests(@transient implicit val sc: SparkContext) extends TemporalAccumuloTests with HadoopSupport with S3Support with TemporalHadoopLoad
