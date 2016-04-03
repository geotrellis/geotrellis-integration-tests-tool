package geotrellis.util

import geotrellis.config.HadoopConfig
import geotrellis.spark.io.hadoop.HdfsUtils
import org.apache.hadoop.fs.{FileSystem, Path}

trait HadoopSupport extends HadoopConfig { self: SparkSupport =>
  val loadParams: Map[String, String]
  val ingestParams: Map[String, String]
  lazy val (hadoopLoadPath, hadoopIngestPath) = loadParams("path") -> ingestParams("path")

  def writeToHdfs(filePath: String, data: Array[Byte]): Unit = {
    val path = new Path(filePath)
    val fs = FileSystem.get(conf)
    val os = fs.create(path)
    os.write(data); os.close(); fs.close()
  }

  //def clearLoadPath = HdfsUtils.deletePath(new Path(hadoopLoadPath), conf)

  def copyToLocal(source: String, dest: String) =
    FileSystem.get(conf).copyToLocalFile(new Path(source), new Path(dest))

  def validationDir = "/data/tmp/"

  def mvValidationTiffLocal: String = {
    /*copyToLocal(validationTiffPath, validationTiffPathLocal);*/ /*validationTiffPathLocal*/
    "/data/tmp/tasmax_amon_BCSD_rcp26_r1i1p1_CONUS_CCSM4_200601-201012-200601120000_0_0.tif"
  }
}
