package geotrellis.test.singleband

import geotrellis.raster._
import geotrellis.raster.io.geotiff.SingleBandGeoTiff
import geotrellis.spark.io.AttributeStore.Fields
import geotrellis.spark.io._
import geotrellis.spark.ingest._
import geotrellis.spark._
import geotrellis.test._
import geotrellis.vector.ProjectedExtent

abstract class SpatialTestEnvironment extends TestEnvironment[ProjectedExtent, SpatialKey] {
  def validate(layerId: LayerId): Unit = {
    val metadata = attributeStore.readLayerAttribute[RasterMetaData](layerId, Fields.metaData)
    val expected = SingleBandGeoTiff(mvValidationTiffLocal)
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
    val diffRaster = Raster(ArrayTile(diffArr, ingestedRaster.cols, ingestedRaster.rows), ingestedRaster.extent)

    writeRaster(ingestedRaster, s"${validationDir}ingested.${this.getClass.getName}")
    writeRaster(expectedRasterResampled, s"${validationDir}expected.${this.getClass.getName}")
    writeRaster(diffRaster, s"${validationDir}diff.${this.getClass.getName}")

    println(s"validation.size.eq: ${ingestedRaster.tile.size == expectedRasterResampled.tile.size}")
    println(s"validation: ${ingestedRaster.tile.toArray().sameElements(expectedRasterResampled.tile.toArray())}")
  }
}
