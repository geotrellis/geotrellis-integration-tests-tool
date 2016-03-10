package geotrellis

import geotrellis.test._
import geotrellis.util.SparkSupport

import com.typesafe.scalalogging.slf4j.LazyLogging

object Main extends App with LazyLogging {
  implicit val sc = SparkSupport.sparkContext

  tests foreach { get =>
    val test = get()
    test.ingest()
    test.combine()
    test.validate()
  }



  logger.info("completed")
  sc.stop()
}
