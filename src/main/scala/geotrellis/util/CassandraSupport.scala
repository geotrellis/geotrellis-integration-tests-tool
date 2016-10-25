package geotrellis.util

import geotrellis.spark.etl.config.{CassandraPath, CassandraProfile}
import geotrellis.spark.io.cassandra.BaseCassandraInstance

trait CassandraSupport extends BackendSupport {
  lazy val cassandraOutputPath = etlConf.output.backend.path match {
    case p: CassandraPath => p
    case p => throw new Exception(s"Not valid output CassandraPath: ${p}")
  }

  @transient lazy val instance = etlConf.output.backend.profile.collect { case profile: CassandraProfile =>
    BaseCassandraInstance(
      profile.hosts.split(","),
      profile.user,
      profile.password,
      profile.replicationStrategy,
      profile.replicationFactor,
      profile.localDc,
      profile.usedHostsPerRemoteDc,
      profile.allowRemoteDCsForLocalConsistencyLevel
    )
  }.get
}
