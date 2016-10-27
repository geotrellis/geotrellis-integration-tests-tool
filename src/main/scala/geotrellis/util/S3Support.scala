package geotrellis.util

import geotrellis.spark.etl.config.S3Path

trait S3Support extends BackendSupport { self: SparkSupport =>
  lazy val (s3InputPath, s3OutputPath) =
    (etlConf.input.backend.path match {
      case p: S3Path => p
      case p => throw new Exception(s"Not valid input S3Path: ${p}")
    }, etlConf.output.backend.path match {
      case p: S3Path => p
      case p => throw new Exception(s"Not valid output S3Path: ${p}")
    })
}
