package geotrellis

import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test._
import geotrellis.util.SparkSupport

import scalaz.Scalaz._
import com.typesafe.scalalogging.slf4j.LazyLogging
import geotrellis.config._

object Main extends LazyLogging {

  def main(args: Array[String]): Unit = {
    implicit val sc = SparkSupport.sparkContext(Config.timeTag, Config.timeFormat)
    val (ss, sm, ts, tm) = Config.splitDataSets

    ss foreach { implicit cfg =>
      singleband.tests foreach { case (_, get) =>
        val test = get()
        test.ingest
        test.combine
        test.validate
      }
    }

    sm foreach { implicit cfg =>
      multiband.tests foreach { case (_, get) =>
        val test = get()
        test.ingest
        test.combine
        test.validate
      }
    }

    ts foreach { implicit cfg =>
      singleband.testsTemporal foreach { case (_, get) =>
        val test = get()
        test.ingest
        test.combine
        test.validate
      }
    }

    tm foreach { implicit cfg =>
      multiband.testsTemporal foreach { case (_, get) =>
        val test = get()
        test.ingest
        test.combine
        test.validate
      }
    }

    logger.info("completed")
    sc.stop()
  }
}
