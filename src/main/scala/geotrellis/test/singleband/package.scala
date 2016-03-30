package geotrellis.test

import org.apache.spark.SparkContext
import com.typesafe.config.{Config => TConfig}

package object singleband {
  def tests(implicit configuration: TConfig, sc: SparkContext) = s3Tests ++ hadoopTests
  def testsTemporal(implicit configuration: TConfig, sc: SparkContext) = s3TestsTemporal ++ hadoopTestsTemporal

  def s3Tests(implicit configuration: TConfig, sc: SparkContext) =
    List(
      () => accumulo.S3IngestTest.apply,
      () => hadoop.S3IngestTest.apply,
      () => s3.S3IngestTest.apply,
      () => file.S3IngestTest.apply
    )

  def hadoopTests(implicit configuration: TConfig, sc: SparkContext) =
    List(
      () => accumulo.HadoopIngestTest.apply,
      () => hadoop.HadoopIngestTest.apply,
      () => s3.HadoopIngestTest.apply,
      () => file.HadoopIngestTest.apply
    )

  def s3TestsTemporal(implicit configuration: TConfig, sc: SparkContext) =
    List(
      () => accumulo.TemporalS3IngestTest.apply,
      () => hadoop.TemporalS3IngestTest.apply,
      () => s3.TemporalS3IngestTest.apply,
      () => file.TemporalS3IngestTest.apply
    )

  def hadoopTestsTemporal(implicit configuration: TConfig, sc: SparkContext) =
    List(
      () => accumulo.TemporalHadoopIngestTest.apply,
      () => hadoop.TemporalHadoopIngestTest.apply,
      () => s3.TemporalHadoopIngestTest.apply,
      () => file.TemporalHadoopIngestTest.apply
    )
}
