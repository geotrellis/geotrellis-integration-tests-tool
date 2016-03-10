package geotrellis

import geotrellis.spark.{RasterRDD, SpaceTimeKey}

package object core {
  implicit class withSpaceTimeRasterRDDMethods(val rdd: RasterRDD[SpaceTimeKey]) extends SpaceTimeRasterRDDMethods
}
