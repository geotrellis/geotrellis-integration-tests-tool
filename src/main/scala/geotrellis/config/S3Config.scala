package geotrellis.config

trait S3Config extends Config {
  lazy val s3bucket = either("s3.bucket", "geotrellis-test")(cfg)
  lazy val s3preifx = either("s3.prefix", "nex-geotiff")(cfg)
  lazy val s3path   = Map("bucket" -> s3bucket, "key" -> s3preifx)
}
