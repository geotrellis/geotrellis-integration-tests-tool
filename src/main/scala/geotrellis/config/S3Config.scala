package geotrellis.config

trait S3Config extends Config {
  lazy val s3Bucket = either("s3.bucket", "geotrellis-test")(cfg)
  lazy val s3Preifx = either("s3.prefix", "nex-geotiff")(cfg)
  lazy val s3Params = Map("bucket" -> s3Bucket, "key" -> s3Preifx)
}
