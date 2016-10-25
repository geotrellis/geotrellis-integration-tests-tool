package geotrellis.config.json.dataset

import cats.data.Xor
import geotrellis.config.json.backend.JCredentials
import geotrellis.spark.etl.config._
import io.circe.generic.auto._
import io.circe.parser._

import scala.util.matching.Regex

case class JConfig(name: String, `type`: JType, path: JPath, ingestOptions: JIngestOptions, validationOptions: JValidationOptions) {

  def getInputParams(jbt: JBackendType, p: String): BackendPath = jbt match {
    case JS3Type => {
      val JConfig.S3UrlRx(_, _, bucket, prefix) = p
      S3Path(p, bucket, prefix)
    }
    case JAccumuloType => AccumuloPath(p)
    case JCassandraType => {
      val List(keyspace, table) = p.split("\\.").toList
      CassandraPath(keyspace, table)
    }
    case JHadoopType | JFileType => HadoopPath(p)
  }

  def getProfile(jbt: JBackendType, jCredentials: JCredentials) = jbt match {
    case JS3Type => `type`.ingestCredentials.map { i => jCredentials.s3.filter { c =>
      c.name == i
    } }.flatMap(_.headOption).map(c => S3Profile(c.name))
    case JAccumuloType => `type`.ingestCredentials.map { i => jCredentials.accumulo.filter { c =>
      c.name == i
    } }.flatMap(_.headOption).map(c => AccumuloProfile(c.name, c.instance, c.zookeepers, c.user, c.password))
    case JCassandraType => `type`.ingestCredentials.map { i => jCredentials.cassandra.filter { c =>
      c.name == i
    } }.flatMap(_.headOption).map(c => CassandraProfile(c.name, c.hosts.mkString(","), c.user, c.password, c.replicationStrategy, c.replicationFactor, c.localDc, c.usedHostsPerRemoteDc, c.allowRemoteDCsForLocalConsistencyLevel))
    case JHadoopType | JFileType => `type`.ingestCredentials.map { i => jCredentials.hadoop.filter { c =>
      c.name == i
    } }.flatMap(_.headOption).map(c => HadoopProfile(c.name))
  }

  def getEtlCfg(jCredentials: JCredentials) = {
    val input = Input(
      name = name,
      format = "geotiff",
      backend = Backend(
        `type`  = BackendInputType.fromString(`type`.loadBackend.name),
        profile = getProfile(`type`.loadBackend, jCredentials),
        path    = getLoadParams
      )
    )

    val output = Output(
      backend = Backend(
        `type` = AccumuloType,
        profile = getProfile(`type`.ingestBackend, jCredentials),
        path = getIngestParams
      ),
      resampleMethod = ingestOptions.resampleMethod,
      reprojectMethod = BufferedReproject, // need to add
      keyIndexMethod = IngestKeyIndexMethod(
        `type`             = ingestOptions.keyIndexMethod.`type`,
        timeTag            = ingestOptions.keyIndexMethod.timeTag,
        timeFormat         = ingestOptions.keyIndexMethod.timeFormat,
        temporalResolution = ingestOptions.keyIndexMethod.temporalResolution.map(_.toInt)
      ),
      tileSize = ingestOptions.layoutScheme.tileSize,
      pyramid = true,
      partitions = None,
      layoutScheme = Some(ingestOptions.layoutScheme.`type`),
      layoutExtent = None,
      crs = Some(ingestOptions.layoutScheme.crs.proj4jCrs.getName),
      resolutionThreshold = Some(ingestOptions.layoutScheme.resolutionThreshold),
      cellSize = None,
      cellType = None,
      encoding = None,
      breaks = None,
      maxZoom = None
    )

    new EtlConf(
      input  = input,
      output = output
    )
  }

  def getLoadParams     = getInputParams(`type`.loadBackend, path.load)
  def getIngestParams   = getInputParams(`type`.ingestBackend, path.ingest)
  def isTemporal        = `type`.ingestType == JTemporalType
  def isSpatial         = `type`.ingestType == JSpatialType
  def isSingleband      = `type`.tileType == JSinglebandType
  def isMultiband       = `type`.tileType == JMultibandType
  def isS3Load          = `type`.loadBackend == JS3Type
  def isHadoopLoad      = `type`.loadBackend == JHadoopType
  def isForIngestBackend(jbt: JBackendType) = jbt == `type`.ingestBackend
}

object JConfig extends Implicits {
  val idRx = "[A-Z0-9]{20}"
  val keyRx = "[a-zA-Z0-9+/]+={0,2}"
  val slug = "[a-zA-Z0-9-]+"
  val S3UrlRx = new Regex(s"""s3://(?:($idRx):($keyRx)@)?($slug)/{0,1}(.*)""", "aws_id", "aws_key", "bucket", "prefix")

  def read(s: String) = decode[JConfig](s) match {
    case Xor.Right(c) => c
    case Xor.Left(e)  => throw new Exception(s"errors during configuration parsing: $e")
  }

  def readList(s: String) = decode[List[JConfig]](s) match {
    case Xor.Right(list) => list
    case Xor.Left(e)     => throw new Exception(s"errors during configuration parsing: $e")
  }
}
