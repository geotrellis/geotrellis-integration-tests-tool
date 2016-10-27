package geotrellis.config

sealed trait TileType {
  val name: String
  override def toString = name
}
case object SinglebandType extends TileType { val name = "singleband" }
case object MultibandType extends TileType { val name = "multiband" }

object TileType {
  def fromName(str: String) = str match {
    case SinglebandType.name => SinglebandType
    case MultibandType.name  => MultibandType
    case s                   => throw new Exception(s"Unsupported tile type: $s")
  }
}
