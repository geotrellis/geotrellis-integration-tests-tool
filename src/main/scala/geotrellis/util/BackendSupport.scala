package geotrellis.util

import geotrellis.spark.etl.config.EtlConf

trait BackendSupport {
  val etlConf: EtlConf
}
