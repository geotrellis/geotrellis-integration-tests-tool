package geotrellis.util

import geotrellis.spark.etl.config.{CassandraPath, CassandraProfile}

trait CassandraSupport extends BackendSupport {
  lazy val cassandraOutputPath = etlConf.output.backend.path match {
    case p: CassandraPath => p
    case p => throw new Exception(s"Not valid output CassandraPath: ${p}")
  }

  @transient lazy val instance = etlConf.output.backend.profile.collect { case profile: CassandraProfile => profile.getInstance }.get
}
