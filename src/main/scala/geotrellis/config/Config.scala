package geotrellis.config

import com.typesafe.config.ConfigFactory

trait Config {
  @transient lazy val cfg = ConfigFactory.load()
}

object Config extends S3Config with AccumuloConfig with HadoopConfig
