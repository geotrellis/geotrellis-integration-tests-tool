package geotrellis.core.poly

import geotrellis.proj4.WebMercator
import geotrellis.raster.io.geotiff.GeoTiff
import geotrellis.raster.{CellGrid, MultibandTile, Raster, Tile}

import shapeless.{::, HNil, Poly2}
import cats.Functor
import cats.syntax.functor._

object PolyWrite extends Poly2 {
  type In[F[_], V <: CellGrid] = F[Raster[V]] :: String :: HNil

  implicit def singleband[F[_]: Functor] = at[F[Raster[Tile]], String] { case (raster, dir) =>
    raster.map(GeoTiff(_, WebMercator).write(s"${dir}.tiff"))
    raster.map(_.tile.renderPng().write(s"${dir}.png"))
  }

  implicit def multiband[F[_]: Functor] = at[F[Raster[MultibandTile]], String] { case (raster, dir) =>
    raster.map(GeoTiff(_, WebMercator).write(s"${dir}.tiff"))
  }
}
