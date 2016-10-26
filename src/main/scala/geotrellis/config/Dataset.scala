package geotrellis.config

import geotrellis.spark.etl.config._

case class Dataset(input: Input, output: Output, validation: Validation, attributes: Attributes) {
  def getEtlConf        = new EtlConf(input, output)
  def isTemporal        = attributes.ingestType == TemporalType
  def isSpatial         = attributes.ingestType == SpatialType
  def isSingleband      = attributes.tileType == SinglebandType
  def isMultiband       = attributes.tileType == MultibandType
  def isS3Input         = input.backend.`type` == S3Type
  def isHadoopInput     = input.backend.`type` == HadoopType
  def isForOutputBackend(bt: BackendType) = bt == output.backend.`type`
}

object Dataset {
  // datasets => (ss, sm, ts, tm)
  def split(datasets: List[Dataset]): (List[Dataset], List[Dataset], List[Dataset], List[Dataset]) =
    (datasets.filter(d => d.isSpatial && d.isSingleband),
     datasets.filter(d => d.isSpatial && d.isMultiband),
     datasets.filter(d => d.isTemporal && d.isSingleband),
     datasets.filter(d => d.isTemporal && d.isMultiband))
}
