package geotrellis.test.validation

import geotrellis.core.spark._
import geotrellis.raster.{ArrayMultibandTile, ArrayTile, CellGrid, MultibandTile, Raster, Tile}
import geotrellis.raster.io.geotiff.{MultibandGeoTiff, SinglebandGeoTiff}
import geotrellis.spark._
import geotrellis.vector.Extent
import geotrellis.core.poly._

import org.apache.spark.rdd.RDD
import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}

import scala.math
import scala.util.Random


trait ValidationUtilities {

  def randomExtentWithin(extent: Extent, sampleScale: Double = 0.10): Extent = {
    assert(sampleScale > 0 && sampleScale <= 1)
    val extentWidth = extent.xmax - extent.xmin
    val extentHeight = extent.ymax - extent.ymin

    val sampleWidth = extentWidth * sampleScale
    val sampleHeight = extentHeight * sampleScale

    val testRandom = Random.nextDouble()
    val subsetXMin = (testRandom * (extentWidth - sampleWidth)) + extent.xmin
    val subsetYMin = (Random.nextDouble() * (extentHeight - sampleHeight)) + extent.ymin

    Extent(subsetXMin, subsetYMin, subsetXMin + sampleWidth, subsetYMin + sampleHeight)
  }
}
