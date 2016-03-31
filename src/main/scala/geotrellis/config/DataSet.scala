package geotrellis.config

import com.typesafe.config.{Config => TConfig}
import geotrellis.core.LayoutSchemeArg
import geotrellis.proj4.CRS
import geotrellis.raster.resample._
import geotrellis.spark.io.index._
import geotrellis.spark.tiling.{FloatingLayoutScheme, LayoutScheme, ZoomedLayoutScheme}

import scala.util.matching.Regex

case class DataSet(name: String,
                   dataSetType: String,
                   tileType: String,
                   ingestType: String,
                   loadPath: String,
                   ingestPath: String,
                   resampleMethod: PointResampleMethod,
                   crs: CRS,
                   tileSize: Int,
                   layoutScheme: LayoutScheme,
                   keyIndexMethod: KeyIndexMethod[_],
                   validationExtentSize: Double,
                   resolutionThreshold: Double) {
  def getInputParams(path: String): Map[String, String] = {
    // s3 case
    if(path.contains("s3n://")) {
      val DataSet.S3UrlRx(_, _, bucket, prefix) = path
      Map("bucket" -> bucket, "key" -> prefix)
    } else if (path.contains("hdfs://")) Map("path" -> path.split("hdfs://")(1)) // hadoop case
    else Map("path" -> path) // file / accumulo cases
  }

  def isS3Load               = loadPath.contains("s3n://")
  def getLoadParams          = getInputParams(loadPath)
  def getIngestParams        = getInputParams(ingestPath)
  def toLayoutSchemeArg      = LayoutSchemeArg((crs, ts) => ZoomedLayoutScheme(crs, ts), crs, tileSize)
  def typedKeyIndexMethod[K] = keyIndexMethod.asInstanceOf[KeyIndexMethod[K]]
  def isS3Ingest(ingestBackend: String): Boolean  = (ingestBackend == dataSetType) && isS3Load
  def nonS3Ingest(ingestBackend: String): Boolean = (ingestBackend == dataSetType) && !isS3Load
  def isTemporal             = ingestType == "temporal"
  def nonTemporal            = !isTemporal
}

object DataSet {
  val idRx = "[A-Z0-9]{20}"
  val keyRx = "[a-zA-Z0-9+/]+={0,2}"
  val slug = "[a-zA-Z0-9-]+"
  val S3UrlRx = new Regex(s"""s3n://(?:($idRx):($keyRx)@)?($slug)/{0,1}(.*)""", "aws_id", "aws_key", "bucket", "prefix")

  implicit def configToDataSet(cfg: TConfig): DataSet                 = fromConfig(cfg)
  implicit def configToLayoutSchemeArg(cfg: TConfig): LayoutSchemeArg = fromConfig(cfg).toLayoutSchemeArg
  implicit def dataSetToLayoutSchemeArg(ds: DataSet): LayoutSchemeArg = ds.toLayoutSchemeArg

  def keyIndexMethodFromString(indexType: String, temporalResolution: Option[Long]): KeyIndexMethod[_] = (indexType, temporalResolution) match {
    case ("rowmajor", None)    => RowMajorKeyIndexMethod
    case ("hilbert", None)     => HilbertKeyIndexMethod
    case ("hilbert", Some(tr)) => HilbertKeyIndexMethod(tr.toInt)
    case ("zorder", None)      => ZCurveKeyIndexMethod
    case ("zorder", Some(tr))  => ZCurveKeyIndexMethod.byMilliseconds(tr)
    case _                     => throw new Exception("unsupported keyIndexMethod definition")
  }

  def layoutSchemeFromString(str: String, crs: CRS, tileSize: Int, resolutionThreshold: Double): LayoutScheme = str match {
    case "floating" => FloatingLayoutScheme(tileSize)
    case "zoomed"   => ZoomedLayoutScheme(crs, tileSize, resolutionThreshold)
    case _          => throw new Exception("unsupported layout scheme definition")
  }

  def resampleMethodFromString(str: String): PointResampleMethod with Product with Serializable = str match {
    case "nearest-neighbor"  => NearestNeighbor
    case "bilinear"          => Bilinear
    case "cubic-convolution" => CubicConvolution
    case "cubic-spline"      => CubicSpline
    case "lanczos"           => Lanczos
    case _                   => throw new Exception("unsupported resample method definition")
  }

  def fromConfig(cfg: TConfig) = {
    val crs                 = CRS.fromName(either("layoutScheme.crs")(cfg))
    val tileSize            = eitherInt("layoutScheme.tileSize")(cfg)
    val resolutionThreshold = eitherDouble("layoutScheme.resolutionThreshold")(cfg)
    val layoutScheme        = layoutSchemeFromString(either("layoutScheme.type")(cfg), crs, tileSize, resolutionThreshold)

    DataSet(
      name                 = either("name")(cfg),
      dataSetType          = either("type")(cfg),
      tileType             = either("tileType")(cfg),
      ingestType           = either("ingestType")(cfg),
      loadPath             = either("loadPath")(cfg),
      ingestPath           = either("ingestPath")(cfg),
      resampleMethod       = resampleMethodFromString(either("resampleMethod")(cfg)),
      crs                  = crs,
      tileSize             = tileSize,
      layoutScheme         = layoutScheme,
      keyIndexMethod       = keyIndexMethodFromString(either("keyIndexMethod.type")(cfg), optionLong("keyIndexMethod.temporalResolution")(cfg)),
      validationExtentSize = eitherDouble("validationExtentSize")(cfg),
      resolutionThreshold  = resolutionThreshold
    )
  }
}
