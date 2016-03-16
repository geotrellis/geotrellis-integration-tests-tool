package geotrellis.test.multiband

import geotrellis.raster.{ArrayTile, Raster}
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import geotrellis.spark._
import geotrellis.spark.io.AttributeStore.Fields
import geotrellis.spark.io._
import geotrellis.spark.ingest._
import geotrellis.test._
import geotrellis.core._

import org.joda.time.DateTime

abstract class TemporalTestEnvironment extends MultibandTestEnvironment[TemporalProjectedExtent, SpaceTimeKey] {
  def validate(layerId: LayerId): Unit = {
    //
  }
}
