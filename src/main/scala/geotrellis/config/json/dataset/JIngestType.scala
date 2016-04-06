package geotrellis.config.json.dataset

sealed trait JIngestType
case object JSpatialType extends JIngestType
case object JTemporalType extends JIngestType

object JIngestType {
  def fromName(str: String) = str match {
    case "spatial"  => JSpatialType
    case "temporal" => JTemporalType
    case s          => throw new Exception(s"unsupported ingest type: $s")
  }
}
