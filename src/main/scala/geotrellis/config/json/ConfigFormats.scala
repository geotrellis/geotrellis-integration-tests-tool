package geotrellis.config.json

import java.time.{LocalDate, ZoneOffset, ZonedDateTime}

import geotrellis.config._
import geotrellis.spark.etl.config.BackendProfile
import geotrellis.spark.etl.config.json._
import spray.json.RootJsonFormat
import spray.json._
import spray.json.DefaultJsonProtocol._

trait ConfigFormats {
  implicit object ZonedDateTimeFormat extends RootJsonFormat[ZonedDateTime] {
    def write(zdt: ZonedDateTime): JsValue = zdt.formatted("yyyy-MM-dd").toJson
    def read(value: JsValue): ZonedDateTime =
      value match {
        case JsString(time) => LocalDate.parse(time).atStartOfDay(ZoneOffset.UTC)
        case _ =>
          throw new DeserializationException("LocalDate must be a valid string.")
      }
  }

  implicit val accumuloProfileFormat = jsonFormat5(Validation)

  implicit object TileTypeFormat extends RootJsonFormat[TileType] {
    def write(tt: TileType): JsValue = tt.name.toJson
    def read(value: JsValue): TileType =
      value match {
        case JsString(tt) => TileType.fromName(tt)
        case _ =>
          throw new DeserializationException("TileType must be a valid string.")
      }
  }

  implicit object IngestTypeFormat extends RootJsonFormat[IngestType] {
    def write(it: IngestType): JsValue = it.name.toJson
    def read(value: JsValue): IngestType =
      value match {
        case JsString(it) => IngestType.fromName(it)
        case _ =>
          throw new DeserializationException("IngestType must be a valid string.")
      }
  }

  implicit val attributesFormat = jsonFormat2(Attributes)

  case class DatasetFormat(bp: Map[String, BackendProfile]) extends RootJsonFormat[Dataset] {
    val iformat = InputFormat(bp)
    val oformat = OutputFormat(bp)
    def write(o: Dataset): JsValue = JsObject(
      "input"      -> iformat.write(o.input),
      "output"     -> oformat.write(o.output),
      "validation" -> o.validation.toJson,
      "attributes" -> o.attributes.toJson
    )

    def read(value: JsValue): Dataset =
      value match {
        case JsObject(fields) =>
          Dataset(
            input      = iformat.read(fields("input")),
            output     = oformat.read(fields("output")),
            validation = fields("validation").convertTo[Validation],
            attributes = fields("attributes").convertTo[Attributes]
          )
        case _ =>
          throw new DeserializationException("Dataset must be a valid json object.")
      }
  }

  case class DatasetsFormat(bp: Map[String, BackendProfile]) extends RootJsonFormat[List[Dataset]] {
    val dformat = DatasetFormat(bp)
    def write(l: List[Dataset]): JsValue = l.map(dformat.write).toJson
    def read(value: JsValue): List[Dataset] =
      value match {
        case JsArray(fields) => fields.toList.map(dformat.read)
        case _ =>
          throw new DeserializationException("Dataset must be a valid json object.")
      }
  }
}
