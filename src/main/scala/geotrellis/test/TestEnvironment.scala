package geotrellis.test

import geotrellis.proj4.WebMercator
import geotrellis.raster.io.geotiff.GeoTiff
import geotrellis.raster.io.geotiff.writer.GeoTiffWriter
import geotrellis.raster.{MultiBandTile, Tile, Raster}
import geotrellis.spark.{LayerId, Metadata}
import geotrellis.util.SparkSupport
import geotrellis.vector.Extent
import org.apache.spark.rdd.RDD

trait TestEnvironment { self: SparkSupport =>
  type I
  type K
  type V
  type M

  val layerName: String
  val zoom: Int

  def ingest(layer: String): Unit
  def read(layerId: LayerId, extent: Option[Extent]): RDD[(K, V)] with Metadata[M]
  def combine(layerId: LayerId): K
  def validate(layerId: LayerId): Unit

  def ingest(): Unit = ingest(layerName)
  def combine(): K = combine(LayerId(layerName, zoom))
  def validate(): Unit = validate(LayerId(layerName, zoom))

  def writeRaster(raster: Raster[Tile], dir: String): Unit = {
    GeoTiffWriter.write(GeoTiff(raster, WebMercator), s"${dir}.tiff")
    raster.tile.renderPng().write(s"${dir}.png")
  }

  def writeMultiBandRaster(raster: Raster[MultiBandTile], dir: String): Unit = {
    GeoTiffWriter.write(GeoTiff(raster, WebMercator), s"${dir}.tiff")
  }
}
