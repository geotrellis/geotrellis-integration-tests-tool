package geotrellis.util

import geotrellis.config.json.backend.JAccumulo
import geotrellis.spark.io.accumulo.AccumuloInstance

trait AccumuloSupport extends BackendSupport {
  lazy val table = ingestParams("table")
  @transient lazy val instance = ingestCredensials.map { case credentials: JAccumulo =>
    AccumuloInstance(
      credentials.instance,
      credentials.zookeepers,
      credentials.user,
      credentials.token
    )
  }.get
}
