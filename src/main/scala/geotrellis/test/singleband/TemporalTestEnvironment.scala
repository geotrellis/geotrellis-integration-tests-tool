package geotrellis.test.singleband

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test._

abstract class TemporalTestEnvironment extends TestEnvironment[TemporalProjectedExtent, SpaceTimeKey, Tile]
