package geotrellis.test

import com.typesafe.scalalogging.slf4j.LazyLogging
import geotrellis.test.accumulo.{S3IngestAccumuloTests, HadoopIngestAccumuloTests}

object Main extends App with LazyLogging {
  val hadoopTest = new HadoopIngestAccumuloTests()
  hadoopTest.spatialIngest
  hadoopTest.combineLayers
  hadoopTest.scStop

  val s3Test = new S3IngestAccumuloTests()
  s3Test.spatialIngest
  s3Test.combineLayers
  s3Test.scStop

  logger.info("completed")
}
