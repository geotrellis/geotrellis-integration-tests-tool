package geotrellis.test

import com.typesafe.scalalogging.slf4j.LazyLogging
import geotrellis.test.accumulo.{TemporalHadoopIngestAccumuloTests, TemporalS3IngestAccumuloTests, S3IngestAccumuloTests, HadoopIngestAccumuloTests}
import geotrellis.test.file.{TemporalS3IngestFileTests, TemporalHadoopIngestFileTests, S3IngestFileTests, HadoopIngestFileTests}
import geotrellis.test.hadoop.{TemporalHadoopIngestHadoopTests, TemporalS3IngestHadoopTests, S3IngestHadoopTests, HadoopIngestHadoopTests}
import geotrellis.test.s3.{TemporalHadoopIngestS3Tests, TemporalS3IngestS3Tests, S3IngestS3Tests, HadoopIngestS3Tests}
import geotrellis.util.SparkSupport

object Main extends App with LazyLogging {
  implicit val sc = SparkSupport.sparkContext

  ((() => new HadoopIngestAccumuloTests()) ::
    (() => new S3IngestAccumuloTests()) ::
    (() => new HadoopIngestHadoopTests()) ::
    (() => new S3IngestHadoopTests()) ::
    (() => new HadoopIngestFileTests()) ::
    (() => new S3IngestFileTests()) ::
    (() => new HadoopIngestS3Tests()) ::
    (() => new S3IngestS3Tests()) ::
    (() => new TemporalHadoopIngestAccumuloTests()) ::
    (() => new TemporalS3IngestAccumuloTests()) ::
    (() => new TemporalHadoopIngestHadoopTests()) ::
    (() => new TemporalS3IngestHadoopTests()) ::
    (() => new TemporalHadoopIngestFileTests()) ::
    (() => new TemporalS3IngestFileTests()) ::
    (() => new TemporalHadoopIngestS3Tests()) ::
    (() => new TemporalS3IngestS3Tests()) :: Nil) foreach { get =>
    val test = get()
    test.ingest
    test.combine
  }

  logger.info("completed")
  sc.stop()
}
