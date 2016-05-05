package geotrellis.test.validation

import geotrellis.core.spark._
import geotrellis.raster._
import geotrellis.raster.io.geotiff.MultibandGeoTiff
import geotrellis.proj4._
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.vector.Extent
import geotrellis.config.json.dataset.JConfig
import geotrellis.util.{Colors, LoggingSummary}

import org.joda.time.DateTime
import org.apache.log4j.Logger

import scala.math._


object MultibandSpaceTime extends ValidationUtilities with LoggingSummary {
  @transient lazy val logger: Logger = Logger.getLogger(this.getClass)

  def sizeAndEquality(
    metadata: TileLayerMetadata[SpaceTimeKey],
    jConfig: JConfig,
    layerId: LayerId,
    dt: Option[DateTime],
    read: (LayerId, Option[Extent]) => MultibandTileLayerRDD[SpaceTimeKey],
    logId: String
  ) = {
    val expected = MultibandGeoTiff(jConfig.validationOptions.tiffLocal)
    val expectedRaster = expected.raster.reproject(expected.crs, metadata.crs)

    val ingestedRaster =
      read(layerId, Some(expectedRaster.extent))
        .stitch(dt)
        .crop(expectedRaster.extent)

    val infoAppender = appendLog(logId) _

    val expectedRasterResampled = expectedRaster.resample(ingestedRaster.rasterExtent)
    val diffRasterList: List[Raster[MultibandTile]] = (0 to expectedRaster.bandCount).map { i =>
      val diffArr =
        ingestedRaster
          .band(i).toArray
          .zip(expectedRasterResampled.band(i).toArray)
          .map { case (v1, v2) => v1 - v2 }
      val diffRaster = Raster(ArrayTile(diffArr, ingestedRaster.cols, ingestedRaster.rows), ingestedRaster.extent)
      infoAppender(s"band($i) validation: ${ingestedRaster.band(i).toArray().sameElements(expectedRasterResampled.band(i).toArray())}")
      Raster(ArrayMultibandTile(diffRaster.tile), diffRaster.extent)
    }.toList

    infoAppender(s"validation.size.eq: ${ingestedRaster.tile.size == expectedRasterResampled.tile.size}")

    (Option(ingestedRaster), Option(expectedRasterResampled), diffRasterList)
  }

  def resampleCorrectness(
      metadata: TileLayerMetadata[SpaceTimeKey],
      jConfig: JConfig,
      layerId: LayerId,
      dt: Option[DateTime],
      read: (LayerId, Option[Extent]) => MultibandTileLayerRDD[SpaceTimeKey],
      logId: String
  ) {
    // The basic steps:
    // 1. establish test parameters
    // 2. establish control and test values
    // 3  compare against ingested+resampled values

    // Control values
    val controlTiff = MultibandGeoTiff(jConfig.validationOptions.tiffLocal)
    val controlRaster = controlTiff.raster
    val controlSampleExtent = randomExtentWithin(controlRaster.extent, jConfig.validationOptions.sampleScale)

    // Transformations
    val transformation = Transform(controlTiff.crs, jConfig.ingestOptions.layoutScheme.crs)
    val invTransformation = Transform(jConfig.ingestOptions.layoutScheme.crs, controlTiff.crs)

    // Test parameters
    val diffThreshold = jConfig.validationOptions.resolutionThreshold
    val ingestSampleExtent = controlSampleExtent.reproject(transformation)

    // Test values
    val testRaster = read(layerId, None)
      .filter().where(Intersects(ingestSampleExtent)).result
      .stitch(dt)


    // Here, we need to:
    // 1. get the RasterExtent from our testRaster
    // 2. iterate over its cols/rows
    //   a. get the value from our testRaster at that location
    //   b. get the value from our controlRaster at the projection of that location
    // 3. Compare those values
    def controlIndicesFromTest(col: Int, row: Int): (Int, Int) = {
      val (x, y) = testRaster.rasterExtent.gridToMap(col, row)
      val (xprime, yprime) = invTransformation(x, y)
      controlRaster.rasterExtent.mapToGrid(xprime, yprime)
    }

    for (band <- 0 until testRaster.bandCount) {

      // Counts (for later)
      val cellCount = testRaster.rasterExtent.cols * testRaster.rasterExtent.rows // cell count
      var diffTotal = 0.0 // Sum of test vs control values
      var nanCount = 0 // Count of NaN values encountered
      var outOfBoundsCount = 0
      // Useful stats (for later)
      var minControl = 0.0
      var maxControl = 0.0
      var minTest = 0.0
      var maxTest = 0.0

      for (testCol <- 0 until testRaster.rasterExtent.cols) {
        for (testRow <- 0 until testRaster.rasterExtent.rows) {
          try {
            val (controlCol, controlRow) = controlIndicesFromTest(testCol, testRow)
            val controlValue = controlRaster.band(band).getDouble(controlCol, controlRow)
            val testValue = testRaster.band(band).getDouble(testCol, testRow)

            if (isData(controlValue) && isNoData(testValue)) nanCount += 1
            else if (isNoData(controlValue) && isData(testValue)) nanCount += 1
            else if (isNoData(controlValue) && isNoData(testValue)) ()
            else {
              minControl = min(minControl, controlValue)
              maxControl = max(maxControl, controlValue)
              minTest = min(minTest, testValue)
              maxTest = max(maxTest, testValue)
              diffTotal += abs(controlValue - testValue)
            }
          } catch {
            case e: java.lang.ArrayIndexOutOfBoundsException =>
              outOfBoundsCount += 1
          }
        }
      }

      val infoAppender = appendLog(logId) _
      val warnAppender = appendLog(logId, Colors.yellow(_)) _
      infoAppender(s"Resample correctness band ${band + 1}")
      if (outOfBoundsCount > 0) warnAppender(s"Index out of bounds errors encounted: $outOfBoundsCount exceptions")
      infoAppender(s"Control tile range: ${maxControl - minControl}; test tile range: ${maxTest - minTest}")
      infoAppender(s"Cells counted: $cellCount; total difference: $diffTotal; difference/cell: ${diffTotal/cellCount}")
      infoAppender(s"Cells counted: $cellCount; NaN differences encountered: $nanCount; percent NaN differences: ${(nanCount.toDouble/cellCount)*100}")
      infoAppender(s"validation.resample.similar: ${(diffTotal/cellCount) < diffThreshold}")
    }
  }
}

