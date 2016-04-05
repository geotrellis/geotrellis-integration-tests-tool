package geotrellis.util

trait HadoopSupport extends BackendSupport { self: SparkSupport =>
  lazy val (hadoopLoadPath, hadoopIngestPath) = loadParams("path") -> ingestParams("path")
}
