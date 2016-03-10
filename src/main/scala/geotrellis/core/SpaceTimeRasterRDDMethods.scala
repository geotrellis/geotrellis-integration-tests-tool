package geotrellis.core

import geotrellis.raster._
import geotrellis.spark._
import geotrellis.vector.Extent

trait SpaceTimeRasterRDDMethods {
  val rdd: RasterRDD[SpaceTimeKey]

  /**
    * Collects and stitches all the tiles in the RasterRDD into one CompositeTile.
    * If a tile is missing from the RDD it will be represented with EmptyTile.
    */
  def stitch(tk: TemporalKey): Raster[Tile] = {
    val tileMap =
      rdd
        .filter { case (key, value) => key.temporalKey == tk }
        .collect()
        .toMap
        .map { case (key, tile) ⇒ key.spatialKey → tile }

    val rmd = rdd.metaData
    val tileCols = rmd.tileLayout.tileCols
    val tileRows = rmd.tileLayout.tileRows

    // discover what I have here, in reality RasterMetaData should reflect this already
    val te = GridBounds.envelope(tileMap.keys)
    val tileExtent: Extent = rdd.metaData.mapTransform(te)

    val tiles = te.coords map { case (col, row) ⇒
      tileMap.getOrElse(col → row, EmptyTile(rmd.cellType, tileCols, tileRows))
    }

    Raster(CompositeTile(tiles, TileLayout(te.width, te.height, tileCols, tileRows)), tileExtent)
  }
}