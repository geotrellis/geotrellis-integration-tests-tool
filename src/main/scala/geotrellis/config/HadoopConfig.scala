package geotrellis.config

trait HadoopConfig extends Config {
  lazy val hadoopPath     = either("hadoop.path", "/geotrellis-integration/")(cfg)
  lazy val hadoopLoadPath = either("hadoop.loadPath", "/geotrellis-integration-load/")(cfg)
}
