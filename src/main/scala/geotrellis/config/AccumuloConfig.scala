package geotrellis.config

import org.apache.accumulo.core.client.security.tokens.PasswordToken

trait AccumuloConfig extends Config {
  lazy val instanceName = either("accumulo.instance", "gis")(null)
  lazy val zookeeper    = either("accumulo.zookeepers", "localhost")(null)
  lazy val user         = either("accumulo.user", "root")(null)
  lazy val password     = either("accumulo.password", "secret")(null)
  def token             = new PasswordToken(password)
}
