package geotrellis.config

import com.typesafe.config.{ConfigFactory, Config => TConfig}

trait Config {
  import Config._
  @transient lazy val cfg = ConfigFactory.load()

  lazy val dataSets                = eitherConfigList("datasets")(cfg)
  lazy val splitDataSets           = splitConfig(dataSets)
  lazy val validationTiff          = either("validation.tiff", "")(cfg)
  lazy val validationDir           = either("validation.dir", "/tmp/")(cfg)
  lazy val validationTiffPathLocal = validationDir + validationTiff
  lazy val timeTag                 = either("timeTag", "ISO_TIME")(cfg)
  lazy val timeFormat              = either("timeTag", "yyyy-MM-dd'T'HH:mm:ss")(cfg)
}

object Config extends S3Config with AccumuloConfig with HadoopConfig with FileConfig {
  val singleband = "singleband"
  val multiband  = "multiband"
  val spatial    = "spatial"
  val temporal   = "temporal"

  def spatialSingleband(cfg: TConfig): Boolean =
    either("tileType", "")(cfg) == singleband && either("ingestType", "")(cfg) == spatial

  def spatialMultiband(cfg: TConfig): Boolean =
    either("tileType", "")(cfg) == multiband && either("ingestType", "")(cfg) == spatial

  def temporalSingleband(cfg: TConfig): Boolean =
    either("tileType", "")(cfg) == singleband && either("ingestType", "")(cfg) == temporal

  def temporalMultiband(cfg: TConfig): Boolean =
    either("tileType", "")(cfg) == multiband && either("ingestType", "")(cfg) == temporal

  // cfgs => (ss, sm, ts, tm)
  def splitConfig(cfgs: List[TConfig]): (List[TConfig], List[TConfig], List[TConfig], List[TConfig]) =
    (cfgs.filter(spatialSingleband),
     cfgs.filter(spatialMultiband),
     cfgs.filter(temporalSingleband),
     cfgs.filter(temporalMultiband))
}
