package geotrellis.util

import geotrellis.config.{Config, S3Config}
import geotrellis.spark.io.s3.S3Client
import org.apache.commons.io.IOUtils

trait S3Support extends S3Config { self: SparkSupport =>
  @transient lazy val s3Client = S3Client.default
  val s3IngestBucket: String
  val s3IngestPrefix: String
  val s3LoadBucket: String
  val s3LoadPrefix: String
  lazy val loadKeys = s3Client.listKeys(s3LoadBucket, s3LoadPrefix)

  def saveS3Keys(func: (String, Array[Byte]) => Unit) =
    loadKeys foreach { key =>
      func(key, IOUtils.toByteArray(s3Client.getObject(s3LoadBucket, key).getObjectContent))
    }
}
