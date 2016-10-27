package geotrellis.util

import geotrellis.spark.etl.config.{HBasePath, HBaseProfile}

trait HBaseSupport extends BackendSupport {
  lazy val hbaseOutputPath = etlConf.output.backend.path match {
    case p: HBasePath => p
    case p => throw new Exception(s"Not valid output HBasePath: ${p}")
  }

  @transient lazy val instance = etlConf.output.backend.profile.collect { case profile: HBaseProfile => profile.getInstance }.get
}
