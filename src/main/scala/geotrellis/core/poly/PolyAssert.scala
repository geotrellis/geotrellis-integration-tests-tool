package geotrellis.core.poly

import geotrellis.raster.{MultibandTile, Tile}

import shapeless.{::, HNil, Poly3}
import com.typesafe.scalalogging.LazyLogging

object PolyAssert extends Poly3 with LazyLogging {

  type In[V] = (V, V) :: ((Int, Int), (Int, Int)) :: Double :: HNil
  type Out   = (Boolean, Double, Boolean)

  implicit def singleband = at[(Tile, Tile), ((Int, Int), (Int, Int)), Double] {
    case ((ingestedRaster, raster), ((icol, irow), (col, row)), threshold) =>
      val v1 = ingestedRaster.getDouble(icol, irow)
      val v2 = raster.getDouble(col, row)

      val isNaN  = v1.isNaN || v2.isNaN
      val delta  = if(isNaN) 0d else math.abs(v1 - v2)
      val result = delta < threshold

      if(result || isNaN) logger.debug("singleband assertion success")
      else {
        logger.debug("singleband assertion failed")
        logger.debug(s"isNaN: ${isNaN}")
        logger.debug(s"delta: ${delta}")
        logger.debug(s"delta < threshold: ${delta < threshold}")
      }

      (isNaN, delta, result)
  }

  implicit def multiband = at[(MultibandTile, MultibandTile), ((Int, Int), (Int, Int)), Double] {
    case ((ingestedRaster, raster), ((icol, irow), (col, row)), threshold) =>
      val v1 = ingestedRaster.combine(_.sum).getDouble(icol, irow)
      val v2 = raster.combine(_.sum).getDouble(col, row)

      val isNaN  = v1.isNaN || v2.isNaN
      val delta  = if(isNaN) 0d else math.abs(v1 - v2)
      val result = delta < threshold

      if(result || isNaN) logger.debug("multiband assertion success")
      else {
        logger.debug("multiband assertion failed")
        logger.debug(s"isNaN: ${isNaN}")
        logger.debug(s"delta: ${delta}")
        logger.debug(s"delta < threshold: ${delta < threshold}")
      }

      (isNaN, delta, result)
  }
}
