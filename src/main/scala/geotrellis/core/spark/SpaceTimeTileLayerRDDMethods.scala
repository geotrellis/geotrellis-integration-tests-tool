package geotrellis.core.spark

import geotrellis.raster._
import geotrellis.spark._
import org.joda.time.DateTime

trait SpaceTimeTileLayerRDDMethods {
  val rdd: TileLayerRDD[SpaceTimeKey]

  def stitch(tk: TemporalKey): Raster[Tile] = {
    rdd.withContext {
      _.filter { case (key, value) => key.temporalKey == tk }
      .map { case (key, tile) => key.spatialKey -> tile }
    }.stitch
  }

  def stitch(dt: Option[DateTime]): Raster[Tile] = stitch(TemporalKey(dt.get))
}