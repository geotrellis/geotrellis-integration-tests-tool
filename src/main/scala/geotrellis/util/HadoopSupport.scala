package geotrellis.util

import geotrellis.spark.etl.config.HadoopPath

trait HadoopSupport extends BackendSupport { self: SparkSupport =>
  lazy val (hadoopInputPath, hadoopOutputPath) =
    (etlConf.input.backend.path match {
      case p: HadoopPath => p
      case p => throw new Exception(s"Not valid input HadoopPath: ${p}")
    }, etlConf.output.backend.path match {
      case p: HadoopPath => p
      case p => throw new Exception(s"Not valid output HadoopPath: ${p}")
    })
}
