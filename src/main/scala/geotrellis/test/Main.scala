package geotrellis.test

import com.typesafe.scalalogging.slf4j.LazyLogging
import geotrellis.test.accumulo.{TemporalS3IngestAccumuloTests, S3IngestAccumuloTests, HadoopIngestAccumuloTests}
import geotrellis.test.hadoop.{TemporalS3IngestHadoopTests, S3IngestHadoopTests, HadoopIngestHadoopTests}
import geotrellis.test.s3.{TemporalS3IngestS3Tests, S3IngestS3Tests, HadoopIngestS3Tests}

object Main extends App with LazyLogging {
  ((() => new HadoopIngestAccumuloTests()) ::
    (() => new S3IngestAccumuloTests()) ::
    (() => new HadoopIngestHadoopTests()) ::
    (() => new S3IngestHadoopTests()) ::
    (() => new HadoopIngestS3Tests()) ::
    (() => new S3IngestS3Tests()) ::
    (() => new TemporalS3IngestAccumuloTests()) ::
    (() => new TemporalS3IngestHadoopTests()) ::
    (() => new TemporalS3IngestS3Tests()) :: Nil) foreach { get =>
    val test = get()
    test.ingest
    test.combine
    test.scStop
  }

  logger.info("completed")
}
