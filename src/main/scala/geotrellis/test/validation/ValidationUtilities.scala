package geotrellis.test.validation

import geotrellis.config.Dataset
import geotrellis.vector.Extent

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

  def validationLogId(dataset: Dataset): String = s"${dataset.input.name}.validate"
}
