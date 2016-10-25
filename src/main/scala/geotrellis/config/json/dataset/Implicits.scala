package geotrellis.config.json.dataset

import geotrellis.proj4.CRS
import geotrellis.raster.resample._

import cats.data.Xor
import io.circe.Decoder

import java.time.ZonedDateTime

trait Implicits {
  implicit val decodeDateTime: Decoder[ZonedDateTime] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      dt => Xor.right(ZonedDateTime.parse(dt))
    }
  }

  implicit val decodeCrs: Decoder[CRS] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      crs => Xor.right(CRS.fromName(crs))
    }
  }

  implicit val decodeJBackendType: Decoder[JBackendType] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      jbt => Xor.right(JBackendType.fromName(jbt))
    }
  }

  implicit val decodeJBackendLoadType: Decoder[JBackendLoadType] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      jbt => Xor.right(JBackendType.fromNameLoad(jbt))
    }
  }

  implicit val decodeJIngestType: Decoder[JIngestType] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      jit => Xor.right(JIngestType.fromName(jit))
    }
  }

  implicit val decodeJTileType: Decoder[JTileType] = Decoder.instance { cursor =>
    cursor.as[String].flatMap {
      jtt => Xor.right(JTileType.fromName(jtt))
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
