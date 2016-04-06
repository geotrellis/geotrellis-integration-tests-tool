package geotrellis.config.json.dataset

sealed trait JBackendType
case object JAccumuloType extends JBackendType
case object JS3Type extends JBackendType
case object JHadoopType extends JBackendType
case object JFileType extends JBackendType

object JBackendType {
  def fromName(str: String) = str match {
    case "accumulo" => JAccumuloType
    case "s3"       => JS3Type
    case "hadoop"   => JHadoopType
    case "file"     => JFileType
    case s          => throw new Exception(s"unsupported backend type: $s")
  }
}
