package geotrellis.config

sealed trait IngestType {
  val name: String
  override def toString = name
}
case object SpatialType extends IngestType { val name = "spatial" }
case object TemporalType extends IngestType { val name = "temporal" }

object IngestType {
  def fromName(str: String) = str match {
    case SpatialType.name  => SpatialType
    case TemporalType.name => TemporalType
    case s                 => throw new Exception(s"Unsupported ingest type: $s")
  }
}
