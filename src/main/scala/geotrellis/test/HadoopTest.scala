package geotrellis.test

import geotrellis.config.json.backend.JCredentials
import geotrellis.config.json.dataset.JConfig
import geotrellis.raster.CellGrid
import geotrellis.spark._
import geotrellis.spark.io.avro.AvroRecordCodec
import geotrellis.spark.io.hadoop._
import geotrellis.spark.tiling.TilerKeyMethods
import geotrellis.vector.ProjectedExtent
import geotrellis.util.{Component, HadoopSupport}

import org.apache.hadoop.fs.Path
import spray.json.JsonFormat

import scala.reflect.ClassTag

abstract class HadoopTest[
  I: ClassTag: ? => TilerKeyMethods[I, K]: Component[?, ProjectedExtent],
  K: SpatialComponent: Boundable: AvroRecordCodec: JsonFormat: ClassTag,
  V <: CellGrid: AvroRecordCodec: ClassTag
](jConfig: JConfig, jCredentials: JCredentials) extends TestEnvironment[I, K, V](jConfig, jCredentials) with HadoopSupport {
  @transient lazy val writer         = HadoopLayerWriter(hadoopOutputPath.path)
  @transient lazy val reader         = HadoopLayerReader(hadoopOutputPath.path)
  @transient lazy val copier         = HadoopLayerCopier(hadoopOutputPath.path)
  @transient lazy val mover          = HadoopLayerMover(hadoopOutputPath.path)
  @transient lazy val reindexer      = HadoopLayerReindexer(hadoopOutputPath.path)
  @transient lazy val deleter        = HadoopLayerDeleter(hadoopOutputPath.path)
  @transient lazy val updater        = HadoopLayerUpdater(hadoopOutputPath.path)
  @transient lazy val attributeStore = HadoopAttributeStore(hadoopOutputPath.path)
}
