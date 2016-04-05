package geotrellis.core.poly

import geotrellis.core.spark._
import geotrellis.raster.{ArrayMultibandTile, ArrayTile, CellGrid, MultibandTile, Raster, Tile}
import geotrellis.raster.io.geotiff.{MultibandGeoTiff, SinglebandGeoTiff}
import geotrellis.spark._
import geotrellis.vector.Extent

import org.apache.spark.rdd.RDD
import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}
import shapeless.{::, HNil, Poly5}

object PolyValidate extends Poly5 {
  @transient lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

  type In[K, V, M]        = TileLayerMetadata[K] :: String  :: LayerId :: Option[DateTime] :: ((LayerId, Option[Extent]) => RDD[(K, V)] with Metadata[M]) :: HNil
  type Out[V <: CellGrid] = (Option[Raster[V]], Option[Raster[V]], List[Raster[V]])

  implicit def spatialSingleband = at[
      TileLayerMetadata[SpatialKey], String, LayerId, Option[DateTime],
      (LayerId, Option[Extent]) => TileLayerRDD[SpatialKey]
    ] {
    case (metadata, tiffLocal, layerId, dt, read) =>
      val expected = SinglebandGeoTiff(tiffLocal)
      val expectedRaster = expected.raster.reproject(expected.crs, metadata.crs)

      val ingestedRaster =
        read(layerId, Some(expectedRaster.extent))
          .stitch
          .crop(expectedRaster.extent)

      val expectedRasterResampled = expectedRaster.resample(ingestedRaster.rasterExtent)
      val diffArr =
        ingestedRaster
          .tile.toArray
          .zip(expectedRasterResampled.tile.toArray)
          .map { case (v1, v2) => v1 - v2 }
      val diffRaster: Raster[Tile] = Raster(ArrayTile(diffArr, ingestedRaster.cols, ingestedRaster.rows), ingestedRaster.extent)

      logger.info(s"validation.size.eq: ${ingestedRaster.tile.size == expectedRasterResampled.tile.size}")
      logger.info(s"validation: ${ingestedRaster.tile.toArray().sameElements(expectedRasterResampled.tile.toArray())}")

      (Option(ingestedRaster), Option(expectedRasterResampled), List(diffRaster))
  }

  implicit def spaceTimeSingleband = at[
      TileLayerMetadata[SpaceTimeKey], String, LayerId, Option[DateTime],
      (LayerId, Option[Extent]) => TileLayerRDD[SpaceTimeKey]
    ] {
    case (metadata, tiffLocal, layerId, dt, read) =>
      val expected = SinglebandGeoTiff(tiffLocal)
      val expectedRaster = expected.raster.reproject(expected.crs, metadata.crs)

      val ingestedRaster =
        read(layerId, Some(expectedRaster.extent))
          .stitch(dt)
          .crop(expectedRaster.extent)

      val expectedRasterResampled = expectedRaster.resample(ingestedRaster.rasterExtent)
      val diffArr =
        ingestedRaster
          .tile.toArray
          .zip(expectedRasterResampled.tile.toArray)
          .map { case (v1, v2) => v1 - v2 }
      val diffRaster: Raster[Tile] = Raster(ArrayTile(diffArr, ingestedRaster.cols, ingestedRaster.rows), ingestedRaster.extent)

      logger.info(s"validation.size.eq: ${ingestedRaster.tile.size == expectedRasterResampled.tile.size}")
      logger.info(s"validation: ${ingestedRaster.tile.toArray().sameElements(expectedRasterResampled.tile.toArray())}")

      (Option(ingestedRaster), Option(expectedRasterResampled), List(diffRaster))
  }

  implicit def spatialMultiband = at[
      TileLayerMetadata[SpatialKey], String, LayerId, Option[DateTime],
      (LayerId, Option[Extent]) => MultibandTileLayerRDD[SpatialKey]
    ] {
    case (metadata, tiffLocal, layerId, dt, read) =>
      val expected = MultibandGeoTiff(tiffLocal)
      val expectedRaster = expected.raster.reproject(expected.crs, metadata.crs)

      val ingestedRaster =
        read(layerId, Some(expectedRaster.extent))
          .stitch
          .crop(expectedRaster.extent)

      val expectedRasterResampled = expectedRaster.resample(ingestedRaster.rasterExtent)
      val diffRasterList: List[Raster[MultibandTile]] = (0 to expectedRaster.bandCount).map { i =>
        val diffArr =
          ingestedRaster
            .band(i).toArray
            .zip(expectedRasterResampled.band(i).toArray)
            .map { case (v1, v2) => v1 - v2 }
        val diffRaster = Raster(ArrayTile(diffArr, ingestedRaster.cols, ingestedRaster.rows), ingestedRaster.extent)
        logger.info(s"band($i) validation: ${ingestedRaster.band(i).toArray().sameElements(expectedRasterResampled.band(i).toArray())}")
        Raster(ArrayMultibandTile(diffRaster.tile), diffRaster.extent)
      }.toList

      logger.info(s"validation.size.eq: ${ingestedRaster.tile.size == expectedRasterResampled.tile.size}")

      (Option(ingestedRaster), Option(expectedRasterResampled), diffRasterList)
  }

  implicit def spaceTimeMultiband = at[
      TileLayerMetadata[SpaceTimeKey], String, LayerId, Option[DateTime],
      (LayerId, Option[Extent]) => MultibandTileLayerRDD[SpaceTimeKey]
    ] {
    case (metadata, tiffLocal, layerId, dt, read) =>
      val expected = MultibandGeoTiff(tiffLocal)
      val expectedRaster = expected.raster.reproject(expected.crs, metadata.crs)

      val ingestedRaster =
        read(layerId, Some(expectedRaster.extent))
          .stitch(dt)
          .crop(expectedRaster.extent)

      val expectedRasterResampled = expectedRaster.resample(ingestedRaster.rasterExtent)
      val diffRasterList: List[Raster[MultibandTile]] = (0 to expectedRaster.bandCount).map { i =>
        val diffArr =
          ingestedRaster
            .band(i).toArray
            .zip(expectedRasterResampled.band(i).toArray)
            .map { case (v1, v2) => v1 - v2 }
        val diffRaster = Raster(ArrayTile(diffArr, ingestedRaster.cols, ingestedRaster.rows), ingestedRaster.extent)
        logger.info(s"band($i) validation: ${ingestedRaster.band(i).toArray().sameElements(expectedRasterResampled.band(i).toArray())}")
        Raster(ArrayMultibandTile(diffRaster.tile), diffRaster.extent)
      }.toList

      logger.info(s"validation.size.eq: ${ingestedRaster.tile.size == expectedRasterResampled.tile.size}")

      (Option(ingestedRaster), Option(expectedRasterResampled), diffRasterList)
  }
}
