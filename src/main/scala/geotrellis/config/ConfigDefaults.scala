package geotrellis.config

sealed abstract trait SType {
  val name: String

  override def toString = name
}

case object Spatial extends SType { val name = "spatial" }
case object Temporal extends SType { val name = "temporal" }
case object S3 extends SType { val name = "s3" }
case object Hadoop extends SType { val name = "hadoop" }

object SType {
  def fromString(path: String) = path match {
    case "spatial"                    => Spatial
    case "temporal"                   => Temporal
    case p if path.contains("s3n://") => S3
    case _                            => Hadoop
  }
}
