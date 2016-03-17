package geotrellis

import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test._
import geotrellis.util.SparkSupport

import com.typesafe.scalalogging.slf4j.LazyLogging

object Main extends App with LazyLogging {
  implicit val sc = SparkSupport.sparkContext

  tests foreach { get =>
    val test = get()
    test.ingest(ZCurveKeyIndexMethod)
    test.combine()
    test.validate()
  }

  logger.info("completed")
  sc.stop()
}
