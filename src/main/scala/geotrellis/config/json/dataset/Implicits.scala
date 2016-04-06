package geotrellis.config.json.dataset

import geotrellis.proj4.CRS
import geotrellis.raster.resample._

import cats.data.Xor
import io.circe.Decoder
import org.joda.time.DateTime

trait Implicits {
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
}
