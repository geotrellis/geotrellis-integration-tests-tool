package geotrellis.core.poly

import geotrellis.core.spark._
import geotrellis.raster.{CellGrid, Raster}
import geotrellis.spark._
import geotrellis.vector.Extent
import geotrellis.test._
import geotrellis.config.Dataset

import org.apache.spark.rdd.RDD
import com.typesafe.scalalogging.LazyLogging
import shapeless.{::, HNil, Poly5}

import java.time.ZonedDateTime

object PolyValidate extends Poly5 with LazyLogging {
  type In[K, V, M]        = TileLayerMetadata[K] :: Dataset :: LayerId :: Option[ZonedDateTime] :: ((LayerId, Option[Extent]) => RDD[(K, V)] with Metadata[M]) :: HNil
  type Out[V <: CellGrid] = (Option[Raster[V]], Option[Raster[V]], List[Raster[V]])

  implicit def spatialSingleband = at[
      TileLayerMetadata[SpatialKey], Dataset, LayerId, Option[ZonedDateTime],
      (LayerId, Option[Extent]) => TileLayerRDD[SpatialKey]
    ] {
    case (metadata, dataset, layerId, dt, read) => {
      validation.SinglebandSpatial.resampleCorrectness(metadata, dataset, layerId, dt, read)
      validation.SinglebandSpatial.sizeAndEquality(metadata, dataset, layerId, dt, read)
    }
  }

  implicit def spaceTimeSingleband = at[
      TileLayerMetadata[SpaceTimeKey], Dataset, LayerId, Option[ZonedDateTime],
      (LayerId, Option[Extent]) => TileLayerRDD[SpaceTimeKey]
    ] {
    case (metadata, dataset, layerId, dt, read) =>
      validation.SinglebandSpaceTime.resampleCorrectness(metadata, dataset, layerId, dt, read)
      validation.SinglebandSpaceTime.sizeAndEquality(metadata, dataset, layerId, dt, read)
  }

  implicit def spatialMultiband = at[
      TileLayerMetadata[SpatialKey], Dataset, LayerId, Option[ZonedDateTime],
      (LayerId, Option[Extent]) => MultibandTileLayerRDD[SpatialKey]
    ] {
    case (metadata, dataset, layerId, dt, read) =>
      validation.MultibandSpatial.resampleCorrectness(metadata, dataset, layerId, dt, read)
      validation.MultibandSpatial.sizeAndEquality(metadata, dataset, layerId, dt, read)
  }

  implicit def spaceTimeMultiband = at[
      TileLayerMetadata[SpaceTimeKey], Dataset, LayerId, Option[ZonedDateTime],
      (LayerId, Option[Extent]) => MultibandTileLayerRDD[SpaceTimeKey]
    ] {
    case (metadata, dataset, layerId, dt, read) =>
      validation.MultibandSpaceTime.resampleCorrectness(metadata, dataset, layerId, dt, read)
      validation.MultibandSpaceTime.sizeAndEquality(metadata, dataset, layerId, dt, read)
  }
}
