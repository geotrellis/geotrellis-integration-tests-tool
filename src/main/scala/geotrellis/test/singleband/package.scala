package geotrellis.test

import geotrellis.config.Dataset
import geotrellis.spark.etl.config._

import org.apache.spark.SparkContext

package object singleband {
  def tests(implicit ds: Dataset, sc: SparkContext) = s3Tests ++ hadoopTests
  def testsTemporal(implicit ds: Dataset, sc: SparkContext) = s3TestsTemporal ++ hadoopTestsTemporal

  def s3Tests(implicit ds: Dataset, sc: SparkContext) =
    List(
      (AccumuloType, () => accumulo.S3IngestTest.apply),
      (CassandraType, () => cassandra.S3IngestTest.apply),
      (HadoopType, () => hadoop.S3IngestTest.apply),
      (S3Type, () => s3.S3IngestTest.apply),
      (FileType, () => file.S3IngestTest.apply)
    ).filter { case (key, _) => ds.isForOutputBackend(key) && ds.isS3Input && ds.isSpatial && ds.isSingleband }

  def hadoopTests(implicit ds: Dataset, sc: SparkContext) =
    List(
      (AccumuloType, () => accumulo.HadoopIngestTest.apply),
      (CassandraType, () => cassandra.HadoopIngestTest.apply),
      (HadoopType, () => hadoop.HadoopIngestTest.apply),
      (S3Type, () => s3.HadoopIngestTest.apply),
      (FileType, () => file.HadoopIngestTest.apply)
    ).filter { case (key, _) => ds.isForOutputBackend(key) && ds.isHadoopInput && ds.isSpatial && ds.isSingleband }

  def s3TestsTemporal(implicit ds: Dataset, sc: SparkContext) =
    List(
      (AccumuloType, () => accumulo.TemporalS3IngestTest.apply),
      (CassandraType, () => cassandra.TemporalS3IngestTest.apply),
      (HadoopType, () => hadoop.TemporalS3IngestTest.apply),
      (S3Type, () => s3.TemporalS3IngestTest.apply),
      (FileType, () => file.TemporalS3IngestTest.apply)
    ).filter { case (key, _) => ds.isForOutputBackend(key) && ds.isS3Input && ds.isTemporal && ds.isSingleband }

  def hadoopTestsTemporal(implicit ds: Dataset, sc: SparkContext) =
    List(
      (AccumuloType, () => accumulo.TemporalHadoopIngestTest.apply),
      (CassandraType, () => cassandra.TemporalHadoopIngestTest.apply),
      (HadoopType, () => hadoop.TemporalHadoopIngestTest.apply),
      (S3Type, () => s3.TemporalHadoopIngestTest.apply),
      (FileType, () => file.TemporalHadoopIngestTest.apply)
    ).filter { case (key, _) => ds.isForOutputBackend(key) && ds.isHadoopInput && ds.isTemporal && ds.isSingleband }
}
