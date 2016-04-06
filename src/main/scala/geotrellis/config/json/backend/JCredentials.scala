package geotrellis.config.json.backend

import cats.data.Xor
import geotrellis.config.json.dataset._
import io.circe.generic.auto._
import io.circe.parser._

case class JCredentials(accumulo: List[JAccumulo], s3: List[JS3], hadoop: List[JHadoop]) extends JBackend {
  def getAccumuloCfgs = accumulo.map(e => e.name -> e).toMap
  def getS3Cfgs       = s3.map(e => e.name -> e).toMap
  def getHadoopCfgs   = hadoop.map(e => e.name -> e).toMap

  def get(backend: JBackendType, credentials: Option[String]) =
    credentials.map((backend match {
      case JS3Type                 => getS3Cfgs
      case JAccumuloType           => getAccumuloCfgs
      case JHadoopType | JFileType => getHadoopCfgs
    })(_))

  def getIngest(jConfig: JConfig) = get(jConfig.`type`.ingestBackend, jConfig.`type`.ingestCredentials)
  def getLoad(jConfig: JConfig)   = get(jConfig.`type`.loadBackend, jConfig.`type`.loadCredentials)
}

object JCredentials {
  def read(s: String) = decode[JCredentials](s) match {
    case Xor.Right(c) => c
    case Xor.Left(e)  => throw new Exception(s"errors during configuration parsing: $e")
  }
}
