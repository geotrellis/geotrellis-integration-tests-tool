package geotrellis.util

import geotrellis.config.HadoopConfig
import geotrellis.spark.io.hadoop.HdfsUtils
import org.apache.hadoop.fs.{FileSystem, Path}

trait HadoopSupport extends HadoopConfig { self: SparkSupport =>
  def writeToHdfs(filePath: String, data: Array[Byte]): Unit = {
    val path = new Path(filePath)
    val fs = FileSystem.get(conf)
    val os = fs.create(path)
    os.write(data); os.close(); fs.close()
  }

  def clearLoadPath = HdfsUtils.deletePath(new Path(hadoopLoadPath), conf)
}
