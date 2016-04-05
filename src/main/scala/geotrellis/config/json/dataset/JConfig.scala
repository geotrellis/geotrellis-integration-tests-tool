package geotrellis.config.json.dataset

import cats.data.Xor
import org.joda.time.DateTime
import io.circe.generic.auto._
import io.circe._
import io.circe.parser._

import scala.util.matching.Regex

case class JConfig(name: String, `type`: JType, path: JPath, ingestOptions: JIngestOptions, validationOptions: JValidationOptions) {
  def getInputParams(str: String, p: String) = str match {
    case "s3" => {
      val JConfig.S3UrlRx(_, _, bucket, prefix) = p
      Map("bucket" -> bucket, "key" -> prefix)
    }
    case "accumulo"        => Map("table" -> p)
    case "hadoop" | "file" => Map("path" -> p)
  }

  def getLoadParams     = getInputParams(`type`.loadBackend, path.load)
  def getIngestParams   = getInputParams(`type`.ingestBackend, path.ingest)
  def isTemporal        = `type`.ingestType == "temporal"
  def isSpatial         = `type`.ingestType == "spatial"
  def isSingleband      = `type`.tileType == "singleband"
  def isMultiband       = `type`.tileType == "multiband"
  def isS3Load          = `type`.loadBackend == "s3"
  def isHadoopLoad      = `type`.loadBackend == "hadoop"
  def isForIngestBackend(str: String) = str == `type`.ingestBackend
}

object JConfig {
  implicit val decodeDateTime: Decoder[DateTime] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      case dt => Xor.right(DateTime.parse(dt))
    }
  }

  val idRx = "[A-Z0-9]{20}"
  val keyRx = "[a-zA-Z0-9+/]+={0,2}"
  val slug = "[a-zA-Z0-9-]+"
  val S3UrlRx = new Regex(s"""s3n://(?:($idRx):($keyRx)@)?($slug)/{0,1}(.*)""", "aws_id", "aws_key", "bucket", "prefix")

  def read(s: String) = decode[JConfig](s) match {
    case Xor.Right(c) => c
    case Xor.Left(e)  => throw new Exception(s"errors during configuration parsing: $e")
  }

  def readList(s: String) = decode[List[JConfig]](s) match {
    case Xor.Right(list) => list
    case Xor.Left(e)     => throw new Exception(s"errors during configuration parsing: $e")
  }
}
