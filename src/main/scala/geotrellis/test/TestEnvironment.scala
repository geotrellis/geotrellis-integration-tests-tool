package geotrellis.test

import geotrellis.spark.{LayerId, Metadata}
import geotrellis.util.SparkSupport
import org.apache.spark.rdd.RDD

trait TestEnvironment { self: SparkSupport =>
  type I
  type K
  type V
  type M

  def ingest(layer: String): Unit
  def read(layerId: LayerId): RDD[(K, V)] with Metadata[M]
  def combineLayers(layerId: LayerId): K

  def ingest: Unit
  def combine: Unit
}

//scala.tuple.blablabal$2123123