package geotrellis.util

import geotrellis.spark.etl.config.{AccumuloPath, AccumuloProfile}
import geotrellis.spark.io.accumulo.AccumuloInstance

trait AccumuloSupport extends BackendSupport {
  lazy val accumuloOutputPath = etlConf.output.backend.path match {
    case p: AccumuloPath => p
    case p => throw new Exception(s"Not valid output AccumuloPath: ${p}")
  }

  @transient lazy val instance = etlConf.output.backend.profile.collect { case profile: AccumuloProfile =>
    AccumuloInstance(
      profile.instance,
      profile.zookeepers,
      profile.user,
      profile.token
    )
  }.get
}
