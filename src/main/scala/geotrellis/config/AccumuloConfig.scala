package geotrellis.config

import org.apache.accumulo.core.client.security.tokens.PasswordToken

trait AccumuloConfig extends Config {
  lazy val instanceName = either("accumulo.instance", "gis")(cfg)
  lazy val zookeeper    = either("accumulo.zookeepers", "localhost")(cfg)
  lazy val user         = either("accumulo.user", "root")(cfg)
  lazy val password     = either("accumulo.password", "secret")(cfg)
  lazy val table        = either("accumulo.table", "gtintegration")(cfg)
  def token             = new PasswordToken(password)
}
