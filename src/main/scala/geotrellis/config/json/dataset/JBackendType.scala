package geotrellis.config.json.dataset

sealed trait JBackendType {
  val name: String

  override def toString = name
}

sealed trait JBackendLoadType extends JBackendType

case object JAccumuloType extends JBackendType {
  val name = "accumulo"
}

case object JCassandraType extends JBackendType {
  val name = "cassandra"
}

case object JS3Type extends JBackendLoadType {
  val name = "s3"
}

case object JHadoopType extends JBackendLoadType {
  val name = "hadoop"
}

case object JFileType extends JBackendType {
  val name = "file"
}

object JBackendType {
  def fromName(str: String) = str match {
    case JAccumuloType.name  => JAccumuloType
    case JCassandraType.name => JCassandraType
    case JS3Type.name        => JS3Type
    case JHadoopType.name    => JHadoopType
    case JFileType.name      => JFileType
    case s                   => throw new Exception(s"unsupported backend type: $s")
  }

  def fromNameLoad(str: String) = str match {
    case JS3Type.name     => JS3Type
    case JHadoopType.name => JHadoopType
    case s                => throw new Exception(s"unsupported load backend type: $s")
  }
}
