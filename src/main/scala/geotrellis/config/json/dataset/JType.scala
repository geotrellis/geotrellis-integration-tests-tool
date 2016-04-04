package geotrellis.config.json.dataset

case class JType(loadBackend: String, ingestBackend: String, tileType: String, ingestType: String, loadCredensials: Option[String] = None, ingsetCredensials: Option[String] = None)
