package geotrellis.config

import com.typesafe.config.ConfigFactory

trait Config {
  @transient lazy val cfg = ConfigFactory.load()

  lazy val dataSets                = eitherConfigList("datasets")(cfg)
  lazy val validationTiff          = either("validation.tiff", "")(cfg)
  lazy val validationDir           = either("validation.dir", "/tmp/")(cfg)
  lazy val validationTiffPathLocal = validationDir + validationTiff
}

object Config extends S3Config with AccumuloConfig with HadoopConfig with FileConfig
