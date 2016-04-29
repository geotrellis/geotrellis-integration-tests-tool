package geotrellis.config.json.backend

import geotrellis.config.json.dataset._

import cats.data.Xor
import io.circe.generic.auto._
import io.circe.parser._

case class JCredentials(accumulo: List[JAccumulo], cassandra: List[JCassandra], s3: List[JS3], hadoop: List[JHadoop]) {
  private def getCfgs[T <: JBackend](b: List[T]) = b.map(e => e.name -> e).toMap

  def getAccumuloCfgs  = getCfgs(accumulo)
  def getCassandraCfgs = getCfgs(cassandra)
  def getS3Cfgs        = getCfgs(s3)
  def getHadoopCfgs    = getCfgs(hadoop)

  def get(backend: JBackendType, credentials: Option[String]) =
    credentials.map((backend match {
      case JS3Type                 => getS3Cfgs
      case JAccumuloType           => getAccumuloCfgs
      case JCassandraType          => getCassandraCfgs
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
