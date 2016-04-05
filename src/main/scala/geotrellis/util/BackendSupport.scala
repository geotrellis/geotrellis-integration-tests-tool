package geotrellis.util

import geotrellis.config.json.backend.JBackend

trait BackendSupport {
  val loadParams: Map[String, String]
  val ingestParams: Map[String, String]
  val loadCredensials: Option[JBackend]
  val ingestCredensials: Option[JBackend]
}
