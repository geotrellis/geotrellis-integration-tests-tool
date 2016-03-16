package geotrellis

import geotrellis.spark.{TileLayerRDD, SpaceTimeKey}

package object core {
  implicit class withSpaceTimeTileLayerRDDMethods(val rdd: TileLayerRDD[SpaceTimeKey]) extends SpaceTimeTileLayerRDDMethods
}
