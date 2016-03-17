package geotrellis.test.singleband

import geotrellis.raster._
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.test._
import geotrellis.vector.ProjectedExtent

abstract class SpatialTestEnvironment extends TestEnvironment[ProjectedExtent, SpatialKey, Tile]
