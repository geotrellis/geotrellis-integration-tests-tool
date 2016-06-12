package geotrellis.config.json.backend

import org.apache.accumulo.core.client.security.tokens.PasswordToken

sealed trait JBackend { val name: String }
case class JHadoop(name: String) extends JBackend
case class JS3(name: String) extends JBackend
case class JCassandra(name: String, hosts: Seq[String], user: String, password: String, keyspace: String, replicationStrategy: String, replicationFactor: Int, localDc: String, usedHostsPerRemoteDc: Int, allowRemoteDCsForLocalConsistencyLevel: Boolean) extends JBackend
case class JAccumulo(name: String, instance: String, zookeepers: String, user: String, password: String) extends JBackend {
  def token = new PasswordToken(password)
}
