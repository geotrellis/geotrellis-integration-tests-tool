package geotrellis

import geotrellis.spark.io._
import geotrellis.test._
import geotrellis.util._
import geotrellis.config._

import scalaz.Scalaz._

object Main extends LoggingSummary {
  def main(args: Array[String]): Unit = {
    implicit val sc = SparkSupport.sparkContext()
    val (ss, sm, ts, tm) = Dataset.split(TestsEtlConf(args))

    ss foreach { implicit cfg =>
      singleband.tests foreach { case (_, get) => get().run }
    }

    sm foreach { implicit cfg =>
      multiband.tests foreach { case (_, get) => get().run }
    }

    ts foreach { implicit cfg =>
      singleband.testsTemporal foreach { case (_, get) => get().run }
    }

    tm foreach { implicit cfg =>
      multiband.testsTemporal foreach { case (_, get) => get().run }
    }

    printSummary("Generic Summary")
    logger.info("completed")
    sc.stop()
  }
}
