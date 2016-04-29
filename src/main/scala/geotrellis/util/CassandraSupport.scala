package geotrellis.util

import geotrellis.config.json.backend.JCassandra
import geotrellis.spark.io.cassandra.BaseCassandraInstance

trait CassandraSupport extends BackendSupport {
  lazy val table = ingestParams("table")
  @transient lazy val instance = ingestCredentials.collect { case credentials: JCassandra =>
    BaseCassandraInstance(
      credentials.hosts,
      credentials.keyspace,
      credentials.user,
      credentials.password,
      credentials.replicationStrategy,
      credentials.replicationFactor
    )
  }.get
}
