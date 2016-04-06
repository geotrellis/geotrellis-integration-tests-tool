package geotrellis.config.json.dataset

case class JType(loadBackend: JBackendType, ingestBackend: JBackendType, tileType: JTileType, ingestType: JIngestType, loadCredentials: Option[String] = None, ingestCredentials: Option[String] = None)
