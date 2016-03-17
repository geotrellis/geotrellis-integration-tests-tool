package geotrellis.test.multiband

import geotrellis.test._

import geotrellis.raster._
import geotrellis.raster.io.geotiff.MultibandGeoTiff
import geotrellis.spark.io.AttributeStore.Fields
import geotrellis.spark.io._
import geotrellis.spark.ingest._
import geotrellis.spark._
import geotrellis.vector.ProjectedExtent

abstract class SpatialTestEnvironment extends TestEnvironment[ProjectedExtent, SpatialKey, MultibandTile]
