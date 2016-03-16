package geotrellis.test.multiband

import geotrellis.test._

import geotrellis.raster._
import geotrellis.raster.io.geotiff.MultibandGeoTiff
import geotrellis.spark.io.AttributeStore.Fields
import geotrellis.spark.io._
import geotrellis.spark.ingest._
import geotrellis.spark._
import geotrellis.vector.ProjectedExtent

abstract class SpatialTestEnvironment extends MultibandTestEnvironment[ProjectedExtent, SpatialKey] {
  def validate(layerId: LayerId): Unit = {
    val metadata = attributeStore.readMetadata[TileLayerMetadata[SpatialKey]](layerId)
    val expected = MultibandGeoTiff(mvValidationTiffLocal)
    val expectedRaster = expected.raster.reproject(expected.crs, metadata.crs)

    val ingestedRaster =
      read(layerId, Some(expectedRaster.extent))
        .stitch
        .crop(expectedRaster.extent)

    val expectedRasterResampled = expectedRaster.resample(ingestedRaster.rasterExtent)
    for (i <- 0 to expectedRaster.bandCount) {
      val diffArr =
        ingestedRaster
          .band(i).toArray
          .zip(expectedRasterResampled.band(i).toArray)
          .map { case (v1, v2) => v1 - v2 }
      val diffRaster = Raster(ArrayTile(diffArr, ingestedRaster.cols, ingestedRaster.rows), ingestedRaster.extent)

      println(s"band($i) validation: ${ingestedRaster.band(i).toArray().sameElements(expectedRasterResampled.band(i).toArray())}")

      writeRaster(diffRaster, s"${validationDir}diff.$i.${this.getClass.getName}")
    }

    writeMultibandRaster(ingestedRaster, s"${validationDir}ingested.${this.getClass.getName}")
    writeMultibandRaster(expectedRasterResampled, s"${validationDir}expected.${this.getClass.getName}")

    println(s"validation.size.eq: ${ingestedRaster.tile.size == expectedRasterResampled.tile.size}")
  }
}
