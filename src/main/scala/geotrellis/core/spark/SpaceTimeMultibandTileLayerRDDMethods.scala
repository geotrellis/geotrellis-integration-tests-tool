package geotrellis.core.spark

import geotrellis.raster._
import geotrellis.spark._

import java.time.ZonedDateTime

trait SpaceTimeMultibandTileLayerRDDMethods {
  val rdd: MultibandTileLayerRDD[SpaceTimeKey]

  def stitch(tk: TemporalKey): Raster[MultibandTile] = {
    rdd.withContext {
      _.filter { case (key, value) => key.temporalKey == tk }
      .map { case (key, tile) => key.spatialKey -> tile }
    }.stitch
  }

  def stitch(dt: Option[ZonedDateTime]): Raster[MultibandTile] = stitch(TemporalKey(dt.get))
}