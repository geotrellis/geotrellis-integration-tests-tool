package geotrellis.core.poly

import geotrellis.raster.{MultibandTile, Tile}
import org.slf4j.{Logger, LoggerFactory}
import shapeless.{::, HNil, Poly3}

object PolyAssert extends Poly3 {
  @transient lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

  type In[V] = (V, V) :: ((Int, Int), (Int, Int)) :: Double :: HNil

  implicit def singleband = at[(Tile, Tile), ((Int, Int), (Int, Int)), Double] {
    case ((ingestedRaster, raster), ((icol, irow), (col, row)), threshold) =>
      val v1 = ingestedRaster.getDouble(icol, irow)
      val v2 = raster.getDouble(col, row)

      assert(v1 - v2 < threshold, "singleband assertion failed")
      logger.info("singleband assertion success")
  }

  implicit def multiband = at[(MultibandTile, MultibandTile), ((Int, Int), (Int, Int)), Double] {
    case ((ingestedRaster, raster), ((icol, irow), (col, row)), threshold) =>
      val v1 = ingestedRaster.combine(_.sum).getDouble(icol, irow)
      val v2 = raster.combine(_.sum).getDouble(col, row)

      assert(v1 - v2 < threshold, "multiband assertion failed")
      logger.info("multiband assertion success")
  }
}
