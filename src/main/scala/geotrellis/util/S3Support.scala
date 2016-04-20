package geotrellis.util

trait S3Support extends BackendSupport { self: SparkSupport =>
  lazy val (s3LoadBucket, s3LoadPrefix)     = loadParams("bucket") -> loadParams("key")
  lazy val (s3IngestBucket, s3IngestPrefix) = ingestParams("bucket") -> ingestParams("key")
}
