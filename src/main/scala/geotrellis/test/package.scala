package geotrellis

import org.apache.spark.SparkContext
import com.typesafe.config.{Config => TConfig}

package object test {
  def tests(implicit configuration: TConfig, sc: SparkContext) = singleband.tests // ::: multiband.tests
}
