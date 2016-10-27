package geotrellis.util

import geotrellis.spark.etl.config.{AccumuloPath, AccumuloProfile}

trait AccumuloSupport extends BackendSupport {
  lazy val accumuloOutputPath = etlConf.output.backend.path match {
    case p: AccumuloPath => p
    case p => throw new Exception(s"Not valid output AccumuloPath: ${p}")
  }

  @transient lazy val instance = etlConf.output.backend.profile.collect { case profile: AccumuloProfile => profile.getInstance }.get
}
