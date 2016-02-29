package geotrellis.test.accumulo

import geotrellis.test.hadoop.HadoopLoad
import geotrellis.util.{HadoopSupport, S3Support}
import org.apache.spark.SparkContext

class HadoopIngestAccumuloTests(@transient implicit val sc: SparkContext) extends AccumuloTests with HadoopSupport with S3Support with HadoopLoad
