package geotrellis

import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.test._
import geotrellis.util.SparkSupport

import scalaz.Scalaz._
import com.typesafe.scalalogging.slf4j.LazyLogging
import geotrellis.config._
import geotrellis.raster.Tile
import geotrellis.test.singleband.hadoop
import geotrellis.vector.ProjectedExtent

object Main extends LazyLogging {
  def main(args: Array[String]): Unit = {
    implicit val sc = SparkSupport.sparkContext(Config.timeTag, Config.timeFormat)
    val dataSetsMap = Config.dataSets.groupBy(c => SType.fromString(c.getString("ingestType")))

    dataSetsMap.get(Spatial) foreach { _.foreach { implicit cfg =>
      SType.fromString("loadType") match {
        case S3     => s3Tests foreach { get =>
          val test = get()
          test.ingest(ZCurveKeyIndexMethod)
          test.combine()
          test.validate()
        }
        case Hadoop => hadoopTests foreach { get =>
          val test = get()
          test.ingest(ZCurveKeyIndexMethod)
          test.combine()
          test.validate()
        }
      }
    } }

    dataSetsMap.get(Temporal) foreach { _.foreach { implicit cfg =>
      SType.fromString("loadType") match {
        case S3     => s3TestsTemporal
        case Hadoop => hadoopTestsTemporal
      }
    } }



    /*val tests = List(() => hadoop.TemporalHadoopIngestTest.apply)

    tests foreach { get =>
      val test = get()
      test.ingest(ZCurveKeyIndexMethod.byYear)
      test.combine()
      test.validate()
    }*/

    /*sl foreach { implicit cfg =>
      tests foreach { get =>
        val test = get()
        test.ingest(ZCurveKeyIndexMethod)
        test.combine()
        test.validate()
      }
    }*/

    logger.info("completed")
    sc.stop()
  }
}
