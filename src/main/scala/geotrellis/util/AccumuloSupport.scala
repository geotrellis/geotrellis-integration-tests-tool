package geotrellis.util

import geotrellis.config.AccumuloConfig
import geotrellis.spark.io.accumulo.AccumuloInstance

trait AccumuloSupport extends AccumuloConfig {
  val loadParams: Map[String, String]
  val ingestParams: Map[String, String]
  lazy val table = ingestParams("table")
  @transient lazy val instance = AccumuloInstance(instanceName, zookeeper, user, token)
}
