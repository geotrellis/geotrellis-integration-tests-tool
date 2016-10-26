package geotrellis.core.poly

import geotrellis.raster.{MultibandTile, Tile}
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.ingest.{Ingest, MultibandIngest}
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.index.KeyIndexMethod
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.vector.ProjectedExtent
import geotrellis.util.Component
import geotrellis.spark.etl.config.Output

import org.apache.spark.rdd.RDD
import com.typesafe.scalalogging.LazyLogging
import shapeless.{::, HNil, Poly5}
import spray.json.JsonFormat

import scala.reflect.ClassTag

object PolyIngest extends Poly5 with LazyLogging {
  type In[K, I, V] = String :: KeyIndexMethod[K] :: Output :: RDD[(I, V)] :: LayerWriter[LayerId] :: HNil

  implicit def singleband[
    I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
    K: ClassTag: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat
  ] = at[String, KeyIndexMethod[K], Output, RDD[(I, Tile)], LayerWriter[LayerId]] {
    case (layer, keyIndexMethod, output, loadTiles, writer) =>
      Ingest[I, K](loadTiles, output.getCrs.get, output.getLayoutScheme, resampleMethod = output.resampleMethod, pyramid = true) { case (rdd, z) =>
        if (z > 0) {
          if (rdd.filter(!_._2.isNoDataTile).count < 1) {
            logger.info(s"rdd.filter(!_._2.isNoDataTile).count: ${rdd.filter(!_._2.isNoDataTile).count}")
            logger.error(s"Incorrect ingest ${layer}")
            throw new Exception(s"Incorrect ingest ${layer}")
          }
        }
        writer.write[K, Tile, TileLayerMetadata[K]](LayerId(layer, z), rdd, keyIndexMethod)
      }
  }

  implicit def multiband[
    I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
    K: ClassTag: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat
  ] = at[String, KeyIndexMethod[K], Output, RDD[(I, MultibandTile)], LayerWriter[LayerId]] {
    case (layer, keyIndexMethod, output, loadTiles, writer) =>
      MultibandIngest[I, K](loadTiles, output.getCrs.get, output.getLayoutScheme, resampleMethod = output.resampleMethod, pyramid = true) { case (rdd, z) =>
        if (z > 0) {
          if (rdd.filter(!_._2.band(0).isNoDataTile).count < 1) {
            logger.info(s"rdd.filter(!_._2.band(0).isNoDataTile).count: ${rdd.filter(!_._2.band(0).isNoDataTile).count}")
            logger.error(s"Incorrect ingest ${layer}")
            throw new Exception(s"Incorrect ingest ${layer}")
          }
        }

        writer.write[K, MultibandTile, TileLayerMetadata[K]](LayerId(layer, z), rdd, keyIndexMethod)
      }
  }
}
