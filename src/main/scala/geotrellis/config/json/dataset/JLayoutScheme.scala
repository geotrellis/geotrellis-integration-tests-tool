package geotrellis.config.json.dataset

import geotrellis.proj4.CRS
import geotrellis.spark.tiling.{FloatingLayoutScheme, LayoutScheme, ZoomedLayoutScheme}

case class JLayoutScheme(`type`: String, crs: CRS, tileSize: Int, resolutionThreshold: Double) {
  lazy val getLayoutScheme: LayoutScheme = `type` match {
    case "floating" => FloatingLayoutScheme(tileSize)
    case "zoomed"   => ZoomedLayoutScheme(crs, tileSize, resolutionThreshold)
    case _          => throw new Exception("unsupported layout scheme definition")
  }
}