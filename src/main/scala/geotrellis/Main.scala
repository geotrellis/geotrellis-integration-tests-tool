package geotrellis

import geotrellis.spark.io._
import geotrellis.test._
import geotrellis.util.{LoggingSummary, SparkSupport}
import geotrellis.config._
import geotrellis.cli.MainOptions

import cats.std.all._
import org.apache.log4j.Logger

object Main extends LoggingSummary {
  @transient lazy val logger: Logger = Logger.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    MainOptions.parse(args) match {
      case Some(config) => {
        implicit val sc = SparkSupport.sparkContext()
        implicit val credentials = Config.credentials(config.credentials)

        val (ss, sm, ts, tm) = Config.splitDataset(config.datasets)

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
      case None => throw new Exception("No valid arguments passed")
    }
  }
}
