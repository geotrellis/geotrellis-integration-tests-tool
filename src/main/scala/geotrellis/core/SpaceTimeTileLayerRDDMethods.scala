package geotrellis.core

import geotrellis.raster._
import geotrellis.spark._

trait SpaceTimeTileLayerRDDMethods {
  val rdd: TileLayerRDD[SpaceTimeKey]

  def stitch(tk: TemporalKey): Raster[Tile] = {
    rdd.withContext {
      _.filter { case (key, value) => key.temporalKey == tk }
      .map { case (key, tile) ⇒ key.spatialKey → tile }
    }.stitch
  }
}