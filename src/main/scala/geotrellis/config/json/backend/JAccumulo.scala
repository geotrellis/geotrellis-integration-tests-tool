package geotrellis.config.json.backend

import org.apache.accumulo.core.client.security.tokens.PasswordToken

case class JAccumulo(name: String, instance: String, zookeepers: String, user: String, password: String) extends JBackend {
  def token = new PasswordToken(password)
}