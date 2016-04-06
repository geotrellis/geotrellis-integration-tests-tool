package geotrellis.config.json.dataset

sealed trait JTileType
case object JSinglebandType extends JTileType
case object JMultibandType extends JTileType

object JTileType {
  def fromName(str: String) = str match {
    case "singleband" => JSinglebandType
    case "multiband"  => JMultibandType
    case s            => throw new Exception(s"unsupported tile type: $s")
  }
}
