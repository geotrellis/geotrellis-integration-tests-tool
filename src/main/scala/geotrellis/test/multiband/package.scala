package geotrellis.test

import geotrellis.config.json.dataset.JConfig
import org.apache.spark.SparkContext

package object multiband {
  def tests(implicit jConfig: JConfig, sc: SparkContext) = s3Tests ++ hadoopTests
  def testsTemporal(implicit jConfig: JConfig, sc: SparkContext) = s3TestsTemporal ++ hadoopTestsTemporal

  def s3Tests(implicit jConfig: JConfig, sc: SparkContext) =
    List(
      ("accumulo", () => accumulo.S3IngestTest.apply),
      ("hadoop", () => hadoop.S3IngestTest.apply),
      ("s3", () => s3.S3IngestTest.apply),
      ("file", () => file.S3IngestTest.apply)
    ).filter { case (key, _) => jConfig.isForIngestBackend(key) && jConfig.isS3Load && jConfig.isSpatial && jConfig.isMultiband }

  def hadoopTests(implicit jConfig: JConfig, sc: SparkContext) =
    List(
      ("accumulo", () => accumulo.HadoopIngestTest.apply),
      ("hadoop", () => hadoop.HadoopIngestTest.apply),
      ("s3", () => s3.HadoopIngestTest.apply),
      ("file", () => file.HadoopIngestTest.apply)
    ).filter { case (key, _) => jConfig.isForIngestBackend(key) && jConfig.isHadoopLoad && jConfig.isSpatial && jConfig.isMultiband }

  def s3TestsTemporal(implicit jConfig: JConfig, sc: SparkContext) =
    List(
      ("accumulo", () => accumulo.TemporalS3IngestTest.apply),
      ("hadoop", () => hadoop.TemporalS3IngestTest.apply),
      ("s3", () => s3.TemporalS3IngestTest.apply),
      ("file", () => file.TemporalS3IngestTest.apply)
    ).filter { case (key, _) => jConfig.isForIngestBackend(key) && jConfig.isS3Load && jConfig.isTemporal && jConfig.isMultiband }

  def hadoopTestsTemporal(implicit jConfig: JConfig, sc: SparkContext) =
    List(
      ("accumulo", () => accumulo.TemporalHadoopIngestTest.apply),
      ("hadoop", () => hadoop.TemporalHadoopIngestTest.apply),
      ("s3", () => s3.TemporalHadoopIngestTest.apply),
      ("file", () => file.TemporalHadoopIngestTest.apply)
    ).filter { case (key, _) => jConfig.isForIngestBackend(key) && jConfig.isHadoopLoad && jConfig.isTemporal && jConfig.isMultiband }
}
