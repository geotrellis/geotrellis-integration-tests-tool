package geotrellis.config.json.dataset

import geotrellis.raster.resample._

case class JIngestOptions(resampleMethod: String, layoutScheme: JLayoutScheme, keyIndexMethod: JKeyIndexMethod) {
  lazy val getResampleMethod: PointResampleMethod = resampleMethod match {
    case "nearest-neighbor"  => NearestNeighbor
    case "bilinear"          => Bilinear
    case "cubic-convolution" => CubicConvolution
    case "cubic-spline"      => CubicSpline
    case "lanczos"           => Lanczos
    case _                   => throw new Exception("unsupported resample method definition")
  }
}
