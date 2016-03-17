package geotrellis.core.functor

import geotrellis.proj4.WebMercator
import geotrellis.raster.io.geotiff.GeoTiff
import geotrellis.raster.{MultibandTile, Raster, Tile}
import shapeless.Poly2

object PolyWrite extends Poly2 {
  implicit def singleband = at[Option[Raster[Tile]], String] { case (raster, dir) =>
    raster.foreach(GeoTiff(_, WebMercator).write(s"${dir}.tiff"))
    raster.foreach(_.tile.renderPng().write(s"${dir}.png"))
  }

  implicit def multiband = at[Option[Raster[MultibandTile]], String] { case (raster, dir) =>
    raster.foreach(GeoTiff(_, WebMercator).write(s"${dir}.tiff"))
  }

  implicit def singlebands = at[List[Raster[Tile]], String] { case (rasters, dir) =>
    rasters.foreach(GeoTiff(_, WebMercator).write(s"${dir}.tiff"))
    rasters.foreach(_.tile.renderPng().write(s"${dir}.png"))
  }

  implicit def multibands = at[List[Raster[MultibandTile]], String] { case (rasters, dir) =>
    rasters.foreach(GeoTiff(_, WebMercator).write(s"${dir}.tiff"))
  }
}
