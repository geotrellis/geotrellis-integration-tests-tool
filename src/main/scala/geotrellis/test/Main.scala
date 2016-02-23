package geotrellis.test

import com.typesafe.scalalogging.slf4j.LazyLogging
import geotrellis.test.accumulo.{S3IngestAccumuloTests, HadoopIngestAccumuloTests}
import geotrellis.test.hadoop.{S3IngestHadoopTests, HadoopIngestHadoopTests}
import geotrellis.test.s3.{S3IngestS3Tests, HadoopIngestS3Tests}

object Main extends App with LazyLogging {
  ((() => new HadoopIngestAccumuloTests()) ::
    (() => new S3IngestAccumuloTests()) ::
    (() => new HadoopIngestHadoopTests()) ::
    (() => new S3IngestHadoopTests()) ::
    (() => new HadoopIngestS3Tests()) ::
    (() => new S3IngestS3Tests()) :: Nil) foreach { get =>
    val test = get()
    test.spatialIngest
    test.combineLayers
    test.scStop
  }

  logger.info("completed")
}
