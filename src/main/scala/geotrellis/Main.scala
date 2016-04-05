package geotrellis

import geotrellis.spark.io._
import geotrellis.test._
import geotrellis.util.SparkSupport
import geotrellis.config._

import com.typesafe.scalalogging.slf4j.LazyLogging
import cats.std.all._

object Main extends LazyLogging {
  def main(args: Array[String]): Unit = {
    implicit val sc = SparkSupport.sparkContext()
    implicit val credensials = Config.credensials("file:///path/to/conf/credensials.json")

    val (ss, sm, ts, tm) = Config.splitDataset("file:///path/to/conf/datasets.json")

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

    logger.info("completed")
    sc.stop()
  }
}
