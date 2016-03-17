package geotrellis.core

import geotrellis.spark.{MultibandTileLayerRDD, SpaceTimeKey, TileLayerRDD}

package object spark {
  implicit class withSpaceTimeTileLayerRDDMethods(val rdd: TileLayerRDD[SpaceTimeKey]) extends SpaceTimeTileLayerRDDMethods
  implicit class withSpaceTimeMultibandTileLayerRDDMethods(val rdd: MultibandTileLayerRDD[SpaceTimeKey]) extends SpaceTimeMultibandTileLayerRDDMethods
}
