package geotrellis.test

import geotrellis.config.json.backend.JCredentials
import geotrellis.config.json.dataset._

import org.apache.spark.SparkContext

package object singleband {
  def tests(implicit jConfig: JConfig, jCredentials: JCredentials, sc: SparkContext) = s3Tests ++ hadoopTests
  def testsTemporal(implicit jConfig: JConfig, jCredentials: JCredentials, sc: SparkContext) = s3TestsTemporal ++ hadoopTestsTemporal

  def s3Tests(implicit jConfig: JConfig, jCredentials: JCredentials, sc: SparkContext) =
    List(
      (JAccumuloType, () => accumulo.S3IngestTest.apply),
      (JCassandraType, () => cassandra.S3IngestTest.apply),
      (JHadoopType, () => hadoop.S3IngestTest.apply),
      (JS3Type, () => s3.S3IngestTest.apply),
      (JFileType, () => file.S3IngestTest.apply)
    ).filter { case (key, _) => jConfig.isForIngestBackend(key) && jConfig.isS3Load && jConfig.isSpatial && jConfig.isSingleband }

  def hadoopTests(implicit jConfig: JConfig, jCredentials: JCredentials, sc: SparkContext) =
    List(
      (JAccumuloType, () => accumulo.HadoopIngestTest.apply),
      (JCassandraType, () => cassandra.HadoopIngestTest.apply),
      (JHadoopType, () => hadoop.HadoopIngestTest.apply),
      (JS3Type, () => s3.HadoopIngestTest.apply),
      (JFileType, () => file.HadoopIngestTest.apply)
    ).filter { case (key, _) => jConfig.isForIngestBackend(key) && jConfig.isHadoopLoad && jConfig.isSpatial && jConfig.isSingleband }

  def s3TestsTemporal(implicit jConfig: JConfig, jCredentials: JCredentials, sc: SparkContext) =
    List(
      (JAccumuloType, () => accumulo.TemporalS3IngestTest.apply),
      (JCassandraType, () => cassandra.TemporalS3IngestTest.apply),
      (JHadoopType, () => hadoop.TemporalS3IngestTest.apply),
      (JS3Type, () => s3.TemporalS3IngestTest.apply),
      (JFileType, () => file.TemporalS3IngestTest.apply)
    ).filter { case (key, _) => jConfig.isForIngestBackend(key) && jConfig.isS3Load && jConfig.isTemporal && jConfig.isSingleband }

  def hadoopTestsTemporal(implicit jConfig: JConfig, jCredentials: JCredentials, sc: SparkContext) =
    List(
      (JAccumuloType, () => accumulo.TemporalHadoopIngestTest.apply),
      (JCassandraType, () => cassandra.TemporalHadoopIngestTest.apply),
      (JHadoopType, () => hadoop.TemporalHadoopIngestTest.apply),
      (JS3Type, () => s3.TemporalHadoopIngestTest.apply),
      (JFileType, () => file.TemporalHadoopIngestTest.apply)
    ).filter { case (key, _) => jConfig.isForIngestBackend(key) && jConfig.isHadoopLoad && jConfig.isTemporal && jConfig.isSingleband }
}
