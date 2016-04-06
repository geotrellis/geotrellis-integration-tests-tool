package geotrellis.config.json.dataset

import geotrellis.raster.resample._
import cats.data.Xor
import geotrellis.proj4.CRS
import org.joda.time.DateTime
import io.circe.generic.auto._
import io.circe._
import io.circe.parser._

import scala.util.matching.Regex

case class JConfig(name: String, `type`: JType, path: JPath, ingestOptions: JIngestOptions, validationOptions: JValidationOptions) {
  def getInputParams(jbt: JBackendType, p: String) = jbt match {
    case JS3Type => {
      val JConfig.S3UrlRx(_, _, bucket, prefix) = p
      Map("bucket" -> bucket, "key" -> prefix)
    }
    case JAccumuloType           => Map("table" -> p)
    case JHadoopType | JFileType => Map("path" -> p)
  }

  def getLoadParams     = getInputParams(`type`.loadBackend, path.load)
  def getIngestParams   = getInputParams(`type`.ingestBackend, path.ingest)
  def isTemporal        = `type`.ingestType == JTemporalType
  def isSpatial         = `type`.ingestType == JSpatialType
  def isSingleband      = `type`.tileType == JSinglebandType
  def isMultiband       = `type`.tileType == JMultibandType
  def isS3Load          = `type`.loadBackend == JS3Type
  def isHadoopLoad      = `type`.loadBackend == JHadoopType
  def isForIngestBackend(jbt: JBackendType) = jbt == `type`.ingestBackend
}

object JConfig {
  implicit val decodeDateTime: Decoder[DateTime] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      case dt => Xor.right(DateTime.parse(dt))
    }
  }

  implicit val decodeCrs: Decoder[CRS] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      case crs => Xor.right(CRS.fromName(crs))
    }
  }

  implicit val decodeJBackendType: Decoder[JBackendType] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      case jbt => Xor.right(JBackendType.fromName(jbt))
    }
  }

  implicit val decodeJIngestType: Decoder[JIngestType] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      case jit => Xor.right(JIngestType.fromName(jit))
    }
  }

  implicit val decodeJTileType: Decoder[JTileType] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      case jtt => Xor.right(JTileType.fromName(jtt))
    }
  }

  implicit val decodeResampleMethod: Decoder[PointResampleMethod] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      case "nearest-neighbor"  => Xor.right(NearestNeighbor)
      case "bilinear"          => Xor.right(Bilinear)
      case "cubic-convolution" => Xor.right(CubicConvolution)
      case "cubic-spline"      => Xor.right(CubicSpline)
      case "lanczos"           => Xor.right(Lanczos)
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
