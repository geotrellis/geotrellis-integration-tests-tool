package geotrellis.s3

import com.amazonaws.services.s3.model.S3Object
import geotrellis.config.{S3Config, Config}
import geotrellis.spark.io.hadoop.HdfsUtils
import geotrellis.spark.io.hadoop.formats.HadoopWritable
import geotrellis.spark.io.s3.S3Client
import geotrellis.util.SparkSupport
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path

trait S3Support extends S3Config {
  lazy val s3Client = S3Client.default
  lazy val keys = s3Client.listKeys(Config.s3bucket, Config.s3preifx)

  def writeToHdfs[T: HadoopWritable](path: Path, conf: Configuration, arr: Array[T]): Unit = {
    val fs = path.getFileSystem(conf)
    fs.mkdirs(path.getParent)
    HdfsUtils.writeArray(path, conf, arr)
  }

  def save(func: (S3Object, String) => Unit) =
    keys foreach { key =>
      func(s3Client.getObject(Config.s3bucket, key), key)
    }
}
