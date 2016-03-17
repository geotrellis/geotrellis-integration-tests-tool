package geotrellis

import org.apache.spark.SparkContext

package object test {
  def tests(implicit sc: SparkContext) = singleband.tests // ::: multiband.tests
}
