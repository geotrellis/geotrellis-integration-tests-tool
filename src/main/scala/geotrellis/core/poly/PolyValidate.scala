package geotrellis.core.poly

import geotrellis.core.spark._
import geotrellis.raster.{CellGrid, Raster}
import geotrellis.spark._
import geotrellis.vector.Extent
import geotrellis.test._
import geotrellis.config.json.dataset.JConfig

import org.apache.spark.rdd.RDD
import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}
import shapeless.{::, HNil, Poly5}

object PolyValidate extends Poly5 {
  @transient lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

  type In[K, V, M]        = TileLayerMetadata[K] :: JConfig  :: LayerId :: Option[DateTime] :: ((LayerId, Option[Extent]) => RDD[(K, V)] with Metadata[M]) :: HNil
  type Out[V <: CellGrid] = (Option[Raster[V]], Option[Raster[V]], List[Raster[V]])

  implicit def spatialSingleband = at[
      TileLayerMetadata[SpatialKey], JConfig, LayerId, Option[DateTime],
      (LayerId, Option[Extent]) => TileLayerRDD[SpatialKey]
    ] {
    case (metadata, jConfig, layerId, dt, read) => {
      validation.SinglebandSpatial.resampleCorrectness(metadata, jConfig, layerId, dt, read)
      validation.SinglebandSpatial.sizeAndEquality(metadata, jConfig, layerId, dt, read)
    }
  }

  implicit def spaceTimeSingleband = at[
      TileLayerMetadata[SpaceTimeKey], JConfig, LayerId, Option[DateTime],
      (LayerId, Option[Extent]) => TileLayerRDD[SpaceTimeKey]
    ] {
    case (metadata, jConfig, layerId, dt, read) =>
      validation.SinglebandSpaceTime.resampleCorrectness(metadata, jConfig, layerId, dt, read)
      validation.SinglebandSpaceTime.sizeAndEquality(metadata, jConfig, layerId, dt, read)
  }

  implicit def spatialMultiband = at[
      TileLayerMetadata[SpatialKey], JConfig, LayerId, Option[DateTime],
      (LayerId, Option[Extent]) => MultibandTileLayerRDD[SpatialKey]
    ] {
    case (metadata, jConfig, layerId, dt, read) =>
      validation.MultibandSpatial.resampleCorrectness(metadata, jConfig, layerId, dt, read)
      validation.MultibandSpatial.sizeAndEquality(metadata, jConfig, layerId, dt, read)
  }

  implicit def spaceTimeMultiband = at[
      TileLayerMetadata[SpaceTimeKey], JConfig, LayerId, Option[DateTime],
      (LayerId, Option[Extent]) => MultibandTileLayerRDD[SpaceTimeKey]
    ] {
    case (metadata, jConfig, layerId, dt, read) =>
      validation.MultibandSpaceTime.resampleCorrectness(metadata, jConfig, layerId, dt, read)
      validation.MultibandSpaceTime.sizeAndEquality(metadata, jConfig, layerId, dt, read)
  }
}
