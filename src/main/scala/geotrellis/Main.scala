package geotrellis

import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test._
import geotrellis.util.SparkSupport

import scalaz.Scalaz._
import com.typesafe.scalalogging.slf4j.LazyLogging
import geotrellis.test.singleband.hadoop

object Main extends App with LazyLogging {
  implicit val sc = SparkSupport.sparkContext

  val tests = List(() => hadoop.TemporalHadoopIngestTest.apply)

  tests foreach { get =>
    val test = get()
    test.ingest(ZCurveKeyIndexMethod.byYear)
    test.combine()
    test.validate()
  }

  logger.info("completed")
  sc.stop()
}
