package geotrellis.util

import geotrellis.config.AccumuloConfig
import geotrellis.config.json.backend.{JAccumulo, JBackend}
import geotrellis.spark.io.accumulo.AccumuloInstance

trait AccumuloSupport extends AccumuloConfig {
  val loadParams: Map[String, String]
  val ingestParams: Map[String, String]
  val loadCredensials: Option[JBackend]
  val ingestCredensials: Option[JBackend]
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
