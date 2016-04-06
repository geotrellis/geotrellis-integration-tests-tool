package geotrellis.util

import geotrellis.config.json.backend.JBackend

trait BackendSupport {
  val loadParams: Map[String, String]
  val ingestParams: Map[String, String]
  val loadCredentials: Option[JBackend]
  val ingestCredentials: Option[JBackend]
}
