package geotrellis.config

import geotrellis.config.json.backend.JCredensials
import geotrellis.config.json.dataset.JConfig
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path

import org.apache.spark.SparkContext

object Config {
  def credensials(filePath: String)(implicit sc: SparkContext)  = JCredensials.read(getJson(filePath, sc.hadoopConfiguration))
  def dataset(filePath: String)(implicit sc: SparkContext)      = JConfig.readList(getJson(filePath, sc.hadoopConfiguration))
  def splitDataset(filePath: String)(implicit sc: SparkContext) = Config.splitConfig(dataset(filePath))

  def getJson(filePath: String, conf: Configuration): String = {
    val path = new Path(filePath)
    val fs = path.getFileSystem(conf)
    val is = fs.open(path)
    val json = scala.io.Source.fromInputStream(is).getLines.mkString(" ")
    is.close(); fs.close(); json
  }

  // cfgs => (ss, sm, ts, tm)
  def splitConfig(cfgs: List[JConfig]): (List[JConfig], List[JConfig], List[JConfig], List[JConfig]) =
    (cfgs.filter(c => c.isSpatial && c.isSingleband),
     cfgs.filter(c => c.isSpatial && c.isMultiband),
     cfgs.filter(c => c.isTemporal && c.isSingleband),
     cfgs.filter(c => c.isTemporal && c.isMultiband))
}
