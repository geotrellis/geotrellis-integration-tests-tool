package geotrellis.config

trait S3Config extends Config {
  lazy val s3Bucket       = either("s3.bucket", "geotrellis-test")(cfg)
  lazy val s3LoadPreifx   = either("s3.loadPrefix", "nex-geotiff")(cfg)
  lazy val s3IngestPreifx = either("s3.ingestPrefix", "gt-integration-test")(cfg)
  lazy val s3Params       = Map("bucket" -> s3Bucket, "key" -> s3LoadPreifx)
}
