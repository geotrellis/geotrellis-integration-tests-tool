package geotrellis.util

import org.apache.hadoop.fs.{FileSystem, Path}

trait HadoopSupport extends BackendSupport { self: SparkSupport =>
  lazy val (hadoopLoadPath, hadoopIngestPath) = loadParams("path") -> ingestParams("path")

  def writeToHdfs(filePath: String, data: Array[Byte]): Unit = {
    val path = new Path(filePath)
    val fs = FileSystem.get(conf)
    val os = fs.create(path)
    os.write(data); os.close(); fs.close()
  }

  def copyToLocal(source: String, dest: String) =
    FileSystem.get(conf).copyToLocalFile(new Path(source), new Path(dest))
}
