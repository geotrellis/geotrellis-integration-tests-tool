package geotrellis.util

import geotrellis.config.{Config, S3Config}
import geotrellis.spark.io.s3.S3Client
import org.apache.commons.io.IOUtils

trait S3Support extends S3Config { self: SparkSupport =>
  @transient lazy val s3Client = S3Client.default
  lazy val keys = s3Client.listKeys(Config.s3Bucket, Config.s3Preifx)

  def saveS3Keys(func: (String, Array[Byte]) => Unit) =
    keys foreach { key =>
      func(key, IOUtils.toByteArray(s3Client.getObject(Config.s3Bucket, key).getObjectContent()))
    }
}
