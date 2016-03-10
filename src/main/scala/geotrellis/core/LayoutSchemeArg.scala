package geotrellis.core

import geotrellis.spark.tiling.LayoutScheme
import geotrellis.vector.io.json.CRS

case class LayoutSchemeArg(crs: CRS, layoutScheme: LayoutScheme, floatingPoint: Boolean = false)
