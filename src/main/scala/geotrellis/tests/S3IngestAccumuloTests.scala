package geotrellis.tests

import geotrellis.accumulo.AccumuloSupport
import geotrellis.hadoop.HadoopSupport
import geotrellis.proj4.WebMercator
import geotrellis.raster.Tile
import geotrellis.s3.S3Support
import geotrellis.spark.etl.s3.GeoTiffS3Input
import geotrellis.spark.ingest._
import geotrellis.spark.io.accumulo.{AccumuloInstance, AccumuloLayerReader, AccumuloLayerWriter}
import geotrellis.spark.io.avro.codecs._
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.io.json._
import geotrellis.spark.tiling.ZoomedLayoutScheme
import geotrellis.spark.{LayerId, RasterMetaData, SpatialKey}
import geotrellis.util.SparkSupport
import geotrellis.vector.ProjectedExtent
import org.apache.spark.rdd.RDD

class S3IngestAccumuloTests extends AccumuloTests {
  val layerName: String = "s3ingest-accumulo"

  def loadTiles: RDD[(I, V)] = {
    println("loading tiles from s3...")
    val s3Input = new GeoTiffS3Input()
    s3Input(s3path)
  }
}
