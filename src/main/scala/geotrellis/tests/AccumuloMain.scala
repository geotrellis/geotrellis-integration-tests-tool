package geotrellis.tests

import com.typesafe.scalalogging.slf4j.LazyLogging

object AccumuloMain extends App with LazyLogging {
  val s3Test = new S3IngestAccumuloTests()
  s3Test.spatialIngest
  s3Test.combineLayers

  val hadoopTest = new HadoopIngestAccumuloTests()
  hadoopTest.spatialIngest
  hadoopTest.combineLayers

  logger.info("finished")
}
