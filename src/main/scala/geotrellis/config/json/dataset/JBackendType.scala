package geotrellis.config.json.dataset

sealed trait JBackendType
sealed trait JBackendLoadType extends JBackendType
case object JAccumuloType extends JBackendType
case object JS3Type extends JBackendLoadType
case object JHadoopType extends JBackendLoadType
case object JFileType extends JBackendType

object JBackendType {
  def fromName(str: String) = str match {
    case "accumulo" => JAccumuloType
    case "s3"       => JS3Type
    case "hadoop"   => JHadoopType
    case "file"     => JFileType
    case s          => throw new Exception(s"unsupported backend type: $s")
  }

  def fromNameLoad(str: String) = str match {
    case "s3"       => JS3Type
    case "hadoop"   => JHadoopType
    case s          => throw new Exception(s"unsupported load backend type: $s")
  }
}
