package geotrellis.config

trait HadoopConfig extends Config {
  lazy val hadoopIngestPath = either("hadoop.ingestPath", "/geotrellis-integration/")(cfg)
  lazy val hadoopLoadPath   = either("hadoop.loadPath", "/geotrellis-integration-load-temporal/")(cfg)
  lazy val hadoopParams     = Map("path" -> hadoopLoadPath)

  lazy val validationTiffPath = hadoopLoadPath + validationTiff
}
