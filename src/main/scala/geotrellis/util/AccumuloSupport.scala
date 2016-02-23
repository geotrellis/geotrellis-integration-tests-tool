package geotrellis.util

import geotrellis.config.AccumuloConfig
import geotrellis.spark.io.accumulo.AccumuloInstance

trait AccumuloSupport extends AccumuloConfig {
  @transient lazy val instance = AccumuloInstance(instanceName, zookeeper, user, token)
}
