package geotrellis.util

import geotrellis.config.json.backend.JAccumulo
import geotrellis.spark.io.accumulo.AccumuloInstance

trait AccumuloSupport extends BackendSupport {
  lazy val table = ingestParams("table")
  @transient lazy val instance = ingestCredensials.map { case credensials: JAccumulo =>
    AccumuloInstance(
      credensials.instance,
      credensials.zookeepers,
      credensials.user,
      credensials.token
    )
  }.get
}
