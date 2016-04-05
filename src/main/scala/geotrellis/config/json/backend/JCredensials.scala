package geotrellis.config.json.backend

import cats.data.Xor
import geotrellis.config.json.dataset.JConfig
import io.circe.generic.auto._
import io.circe.parser._

case class JCredensials(accumulo: List[JAccumulo], s3: List[JS3], hadoop: List[JHadoop]) extends JBackend {
  def getAccumuloCfgs = accumulo.map(e => e.name -> e).toMap
  def getS3Cfgs       = s3.map(e => e.name -> e).toMap
  def getHadoopCfgs   = hadoop.map(e => e.name -> e).toMap

  def get(backend: String, credentials: Option[String]) =
    credentials.map((backend match {
      case "s3"       => getS3Cfgs
      case "accumulo" => getAccumuloCfgs
      case "hadoop"   => getHadoopCfgs
    })(_))

  def getIngest(jConfig: JConfig) = get(jConfig.`type`.ingestBackend, jConfig.`type`.ingestCredensials)
  def getLoad(jConfig: JConfig)   = get(jConfig.`type`.loadBackend, jConfig.`type`.loadCredensials)
}

object JCredensials {
  def read(s: String) = decode[JCredensials](s) match {
    case Xor.Right(c) => c
    case Xor.Left(e)  => throw new Exception(s"errors during configuration parsing: $e")
  }
}
