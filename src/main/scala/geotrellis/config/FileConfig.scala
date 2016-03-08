package geotrellis.config

trait FileConfig extends Config {
  lazy val fileIngestPath = either("file.ingestPath", "/geotrellis-integration/")(cfg)
  lazy val fileLoadPath   = either("file.loadPath", "/geotrellis-integration-load/")(cfg)
}
