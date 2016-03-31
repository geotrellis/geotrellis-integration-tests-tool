package geotrellis.test

import geotrellis.config.DataSet

import org.apache.spark.SparkContext
import com.typesafe.config.{Config => TConfig}

package object singleband {
  def tests(implicit configuration: TConfig, sc: SparkContext) =
    (s3Tests ++ hadoopTests) filter (_ => (configuration: DataSet).nonTemporal)

  def testsTemporal(implicit configuration: TConfig, sc: SparkContext) =
    (s3TestsTemporal ++ hadoopTestsTemporal) filter (_ => (configuration: DataSet).isTemporal)

  def s3Tests(implicit configuration: TConfig, sc: SparkContext) =
    List(
      ("accumulo", () => accumulo.S3IngestTest.apply),
      ("hadoop", () => hadoop.S3IngestTest.apply),
      ("s3", () => s3.S3IngestTest.apply),
      ("file", () => file.S3IngestTest.apply)
    ).filter { case(key, _) => (configuration: DataSet).isS3Ingest(key) }

  def hadoopTests(implicit configuration: TConfig, sc: SparkContext) =
    List(
      ("accumulo", () => accumulo.HadoopIngestTest.apply),
      ("hadoop", () => hadoop.HadoopIngestTest.apply),
      ("s3", () => s3.HadoopIngestTest.apply),
      ("file", () => file.HadoopIngestTest.apply)
    ).filter { case(key, _) => (configuration: DataSet).nonS3Ingest(key) }

  def s3TestsTemporal(implicit configuration: TConfig, sc: SparkContext) =
    List(
      ("accumulo", () => accumulo.TemporalS3IngestTest.apply),
      ("hadoop", () => hadoop.TemporalS3IngestTest.apply),
      ("s3", () => s3.TemporalS3IngestTest.apply),
      ("file", () => file.TemporalS3IngestTest.apply)
    ).filter { case(key, _) => (configuration: DataSet).isS3Ingest(key) }

  def hadoopTestsTemporal(implicit configuration: TConfig, sc: SparkContext) =
    List(
      ("accumulo", () => accumulo.TemporalHadoopIngestTest.apply),
      ("hadoop", () => hadoop.TemporalHadoopIngestTest.apply),
      ("s3", () => s3.TemporalHadoopIngestTest.apply),
      ("file", () => file.TemporalHadoopIngestTest.apply)
    ).filter { case(key, _) => (configuration: DataSet).nonS3Ingest(key) }
}
