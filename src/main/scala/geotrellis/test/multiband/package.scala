package geotrellis.test

import org.apache.spark.SparkContext

package object multiband {
  def tests(implicit sc: SparkContext) =
    List(
      () => accumulo.HadoopIngestTests.apply,
      () => accumulo.S3IngestTests.apply,
      () => hadoop.HadoopIngestTests.apply,
      () => hadoop.S3IngestTests.apply,
      () => s3.HadoopIngestTests.apply,
      () => s3.S3IngestTests.apply,
      () => file.HadoopIngestTests.apply,
      () => file.S3IngestTests.apply/*,
      () => accumulo.TemporalS3IngestTests.apply,
      () => hadoop.TemporalS3IngestTests.apply,
      () => s3.TemporalS3IngestTests.apply,
      () => file.TemporalS3IngestTests.apply*/
    )
}
