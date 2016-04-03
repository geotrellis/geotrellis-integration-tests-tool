package geotrellis.config.json.dataset

import geotrellis.proj4.CRS
import geotrellis.spark.tiling.{FloatingLayoutScheme, LayoutScheme, ZoomedLayoutScheme}

case class JLayoutScheme(`type`: String, crs: String, tileSize: Int, resolutionThreshold: Double) {
  lazy val getCrs: CRS = CRS.fromName(crs)
  lazy val getLayoutScheme: LayoutScheme = `type` match {
    case "floating" => FloatingLayoutScheme(tileSize)
    case "zoomed"   => ZoomedLayoutScheme(getCrs, tileSize, resolutionThreshold)
    case _          => throw new Exception("unsupported layout scheme definition")
  }
}