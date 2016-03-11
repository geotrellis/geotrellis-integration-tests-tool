package geotrellis.core

import geotrellis.proj4.{CRS, WebMercator}
import geotrellis.spark.tiling.{ZoomedLayoutScheme, LayoutScheme}

case class LayoutSchemeArg(
  getLayoutScheme: (CRS, Int) => LayoutScheme,
  crs            : CRS = WebMercator,
  tileSize       : Int = 256) {
  def layoutScheme = getLayoutScheme(crs, tileSize)
}

object LayoutSchemeArg {
  def default = LayoutSchemeArg((crs, ts) => ZoomedLayoutScheme(crs, ts))
}
