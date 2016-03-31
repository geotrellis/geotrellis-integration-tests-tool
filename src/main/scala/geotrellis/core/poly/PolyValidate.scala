package geotrellis.core.poly

import geotrellis.config.DataSet
import geotrellis.core.spark._
import geotrellis.raster.{ArrayMultibandTile, ArrayTile, MultibandTile, Raster, Tile}
import geotrellis.raster.io.geotiff.{MultibandGeoTiff, SinglebandGeoTiff}
import geotrellis.spark._
import geotrellis.raster._
import geotrellis.spark.io._
import geotrellis.spark.stitch._
import geotrellis.vector.Extent
import geotrellis.vector.reproject._
import geotrellis.proj4._

import org.apache.spark.rdd._
import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}
import shapeless.Poly6

import scala.util.Random
import scala.math.abs

object RandomExtentWithin {
  def apply(extent: Extent, sampleSize: Double): Extent = {
    val extentWidth = extent.xmax - extent.xmin
    val extentHeight = extent.ymax - extent.ymin

    val subXMin = Random.nextDouble() * (extentWidth - sampleSize)
    val subYMin = Random.nextDouble() * (extentHeight - sampleSize)

    Extent(subXMin, subYMin, subXMin + sampleSize, subYMin + sampleSize)
  }
}

object PolyValidate extends Poly6 {
  @transient lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

  implicit def spatialSingleband = at[
      DataSet, TileLayerMetadata[SpatialKey], String, LayerId,
      Option[DateTime], (LayerId, Option[Extent]) => TileLayerRDD[SpatialKey]
    ] {
    case (dataset, metadata, mvValidationTiffLocal, layerId, dt, read) =>
      // The basic steps:
      // 1. establish test parameters
      // 2. establish control and test values
      // 3  compare against ingested+resampled values

      // Test parameters
      val diffThreshold = dataset.resolutionThreshold
      val transformation = Transform(metadata.crs, dataset.crs)
      val inputSampleExtent = RandomExtentWithin(metadata.layoutExtent, dataset.validationExtentSize)
      val ingestSampleExtent = inputSampleExtent.reproject(transformation)

      // Control values
      val controlRaster = SinglebandGeoTiff(mvValidationTiffLocal).raster

      // Test values
      val testRaster: Raster[Tile] = read(layerId, None)
        .filter().where(Intersects(ingestSampleExtent)).result
        .stitch


      // Here, we need to:
      // 1. get the RasterExtent from our controlRaster
      // 2. iterate over its cols/rows
      //   a. get the value from our controlRaster at that location
      //   b. get the value from our testRaster at the projection of that location
      // 3. Compare those values
      def testIndicesFromControl(col: Int, row: Int): (Int, Int) = {
        val (x, y) = controlRaster.rasterExtent.gridToMap(col, row)
        val (xprime, yprime) = transformation(x, y)
        testRaster.rasterExtent.mapToGrid(xprime, yprime)
      }

      // Comparison
      val diffTile = DoubleArrayTile.empty(controlRaster.rasterExtent.cols, controlRaster.rasterExtent.rows)
      for (col <- 0 until controlRaster.rasterExtent.cols) {
        for (row <- 0 until controlRaster.rasterExtent.rows) {
          val (testCol, testRow) = testIndicesFromControl(col, row)
          assert(abs(controlRaster.tile.getDouble(col, row) - testRaster.tile.getDouble(testCol, testRow)) <= diffThreshold,
                 s"Difference between control value (${controlRaster.tile.getDouble(col, row)}) and test value (${testRaster.tile.getDouble(testCol, testRow)}) greater than threshold (${diffThreshold})")
          diffTile.setDouble(col, row, controlRaster.tile.getDouble(col, row) - testRaster.tile.getDouble(testCol, testRow))
        }
      }

      val diffRaster: Raster[Tile] = Raster(diffTile, controlRaster.extent)
      (Option(testRaster), Option(controlRaster), List(diffRaster))
  }

  implicit def spaceTimeSingleband = at[
      DataSet, TileLayerMetadata[SpaceTimeKey], String, LayerId,
      Option[DateTime], (LayerId, Option[Extent]) => TileLayerRDD[SpaceTimeKey]
    ] {
    case (dataset, metadata, mvValidationTiffLocal, layerId, dt, read) =>
      // Test params
      val diffThreshold = dataset.resolutionThreshold
      val transformation = Transform(metadata.crs, dataset.crs)
      val inputSampleExtent = RandomExtentWithin(metadata.layoutExtent, dataset.validationExtentSize)
      val ingestSampleExtent = inputSampleExtent.reproject(transformation)

      // Control values
      val controlRaster = SinglebandGeoTiff(mvValidationTiffLocal).raster

      // Test values
      val testRaster: Raster[Tile] = read(layerId, None)
        .filter().where(Intersects(ingestSampleExtent)).result
        .stitch(dt)


      def testIndicesFromControl(col: Int, row: Int): (Int, Int) = {
        val (x, y) = controlRaster.rasterExtent.gridToMap(col, row)
        val (xprime, yprime) = transformation(x, y)
        testRaster.rasterExtent.mapToGrid(xprime, yprime)
      }

      // Comparison
      val diffTile = DoubleArrayTile.empty(controlRaster.rasterExtent.cols, controlRaster.rasterExtent.rows)
      for (col <- 0 until controlRaster.rasterExtent.cols) {
        for (row <- 0 until controlRaster.rasterExtent.rows) {
          val (testCol, testRow) = testIndicesFromControl(col, row)
          assert(abs(controlRaster.tile.getDouble(col, row) - testRaster.tile.getDouble(testCol, testRow)) <= diffThreshold,
                 s"Difference between control value (${controlRaster.tile.getDouble(col, row)}) and test value (${testRaster.tile.getDouble(testCol, testRow)}) greater than threshold (${diffThreshold})")
          diffTile.setDouble(col, row, controlRaster.tile.getDouble(col, row) - testRaster.tile.getDouble(testCol, testRow))
        }
      }

      val diffRaster: Raster[Tile] = Raster(diffTile, controlRaster.extent)
      (Option(testRaster), Option(controlRaster), List(diffRaster))
  }

  implicit def spatialMultiband = at[
      DataSet, TileLayerMetadata[SpatialKey], String, LayerId,
      Option[DateTime], (LayerId, Option[Extent]) => MultibandTileLayerRDD[SpatialKey]
    ] {
    case (dataset, metadata, mvValidationTiffLocal, layerId, dt, read) =>
      // Test params
      val diffThreshold = dataset.resolutionThreshold
      val transformation = Transform(metadata.crs, dataset.crs)
      val inputSampleExtent = RandomExtentWithin(metadata.layoutExtent, dataset.validationExtentSize)
      val ingestSampleExtent = inputSampleExtent.reproject(transformation)

      // Control values
      val controlRaster: Raster[MultibandTile] = MultibandGeoTiff(mvValidationTiffLocal).raster

      // Test values
      val testRaster: Raster[MultibandTile] = read(layerId, None)
        .filter().where(Intersects(ingestSampleExtent)).result
        .stitch

      def testIndicesFromControl(col: Int, row: Int): (Int, Int) = {
        val (x, y) = controlRaster.rasterExtent.gridToMap(col, row)
        val (xprime, yprime) = transformation(x, y)
        testRaster.rasterExtent.mapToGrid(xprime, yprime)
      }

      // Comparison
      val diffTiles: List[DoubleArrayTile] = (0 to controlRaster.bandCount).toList.map { i =>
        DoubleArrayTile.empty(controlRaster.rasterExtent.cols, controlRaster.rasterExtent.rows)
      }
      for (band <- 0 to controlRaster.bandCount) {
        for (col <- 0 until controlRaster.rasterExtent.cols) {
          for (row <- 0 until controlRaster.rasterExtent.rows) {
            val (testCol, testRow) = testIndicesFromControl(col, row)
            assert(abs(controlRaster.tile.band(band).getDouble(col, row) - testRaster.tile.band(band).getDouble(testCol, testRow)) <= diffThreshold,
                   s"Difference between control value (${controlRaster.tile.band(band).getDouble(col, row)}) and test value (${testRaster.tile.band(band).getDouble(testCol, testRow)}) greater than threshold (${diffThreshold})")
            diffTiles(band).setDouble(col, row, controlRaster.tile.band(band).getDouble(col, row) - testRaster.tile.band(band).getDouble(testCol, testRow))
          }
        }
      }
      val diffRasters: List[Raster[MultibandTile]] =
        diffTiles.map { t => Raster(ArrayMultibandTile(t), controlRaster.extent) }.toList
      (Option(testRaster), Option(controlRaster), diffRasters)
  }

  implicit def spaceTimeMultiband = at[
      DataSet, TileLayerMetadata[SpaceTimeKey], String, LayerId,
      Option[DateTime], (LayerId, Option[Extent]) => MultibandTileLayerRDD[SpaceTimeKey]
    ] {
    case (dataset, metadata, mvValidationTiffLocal, layerId, dt, read) =>
      // Test params
      val diffThreshold = dataset.resolutionThreshold
      val transformation = Transform(metadata.crs, dataset.crs)
      val inputSampleExtent = RandomExtentWithin(metadata.layoutExtent, dataset.validationExtentSize)
      val ingestSampleExtent = inputSampleExtent.reproject(transformation)

      // Control values
      val controlRaster: Raster[MultibandTile] = MultibandGeoTiff(mvValidationTiffLocal).raster

      // Test values
      val testRaster: Raster[MultibandTile] = read(layerId, None)
        .filter().where(Intersects(ingestSampleExtent)).result
        .stitch(dt)

      def testIndicesFromControl(col: Int, row: Int): (Int, Int) = {
        val (x, y) = controlRaster.rasterExtent.gridToMap(col, row)
        val (xprime, yprime) = transformation(x, y)
        testRaster.rasterExtent.mapToGrid(xprime, yprime)
      }

      // Comparison
      val diffTiles: List[DoubleArrayTile] = (0 to controlRaster.bandCount).toList.map { i =>
        DoubleArrayTile.empty(controlRaster.rasterExtent.cols, controlRaster.rasterExtent.rows)
      }
      for (band <- 0 to controlRaster.bandCount) {
        for (col <- 0 until controlRaster.rasterExtent.cols) {
          for (row <- 0 until controlRaster.rasterExtent.rows) {
            val (testCol, testRow) = testIndicesFromControl(col, row)
            assert(abs(controlRaster.tile.band(band).getDouble(col, row) - testRaster.tile.band(band).getDouble(testCol, testRow)) <= diffThreshold,
                   s"Difference between control value (${controlRaster.tile.band(band).getDouble(col, row)}) and test value (${testRaster.tile.band(band).getDouble(testCol, testRow)}) greater than threshold (${diffThreshold})")
            diffTiles(band).setDouble(col, row, controlRaster.tile.band(band).getDouble(col, row) - testRaster.tile.band(band).getDouble(testCol, testRow))
          }
        }
      }
      val diffRasters: List[Raster[MultibandTile]] =
        diffTiles.map { t => Raster(ArrayMultibandTile(t), controlRaster.extent) }.toList
      (Option(testRaster), Option(controlRaster), diffRasters)
  }
}
