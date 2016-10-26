package geotrellis.config

import geotrellis.spark.etl.config._
import geotrellis.spark.etl.config.json._
import geotrellis.config.json._

import org.apache.spark.SparkContext
import com.github.fge.jackson.JsonLoader
import spray.json._

object TestsEtlConf extends TestsEtlConf

trait TestsEtlConf extends ConfigParse {
  val help = s"""
               |${Info.name} ${Info.version}
               |
               |Usage: ${Info.name} [options]
               |
               |  --datasets <value>
               |        datasets is a non-empty String property
               |  --backend-profiles <value>
               |        backend-profiles is a non-empty String property
               |  --help
               |        prints this usage text
             """.stripMargin

  val requiredFields = Set('datasets, 'backendProfiles)

  val backendProfilesSchema = schemaFactory.getJsonSchema(JsonLoader.fromResource("/backend-profiles-schema.json"))
  val datasetsSchema        = schemaFactory.getJsonSchema(JsonLoader.fromResource("/datasets-schema.json"))

  def nextOption(map: Map[Symbol, String], list: Seq[String]): Map[Symbol, String] =
    list.toList match {
      case Nil => map
      case "--datasets" :: value :: tail =>
        nextOption(map ++ Map('datasets -> value), tail)
      case "--backend-profiles" :: value :: tail =>
        nextOption(map ++ Map('backendProfiles -> value), tail)
      case "--help" :: tail => {
        println(help)
        sys.exit(1)
      }
      case option :: tail => {
        println(s"Unknown option ${option}")
        println(help)
        sys.exit(1)
      }
    }

  def apply(args: Seq[String])(implicit sc: SparkContext): List[Dataset] = {
    val m = parse(args)

    if(m.keySet != requiredFields) {
      println(s"missing required field(s): ${(requiredFields -- m.keySet).mkString(", ")}, use --help command to get additional information about input options.")
      sys.exit(1)
    }

    val(backendProfiles, datasets) = m('backendProfiles) -> m('datasets)

    val backendProfilesValidation = backendProfilesSchema.validate(JsonLoader.fromString(backendProfiles), true)
    val datasetsValidation        = datasetsSchema.validate(JsonLoader.fromString(datasets), true)

    if(!backendProfilesValidation.isSuccess || !datasetsValidation.isSuccess) {
      if(!backendProfilesValidation.isSuccess) {
        println("backendProfiles validation error:")
        println(backendProfilesValidation)
      }
      if(!datasetsValidation.isSuccess) {
        println("datasets validation error:")
        println(datasetsValidation)
      }
      sys.exit(1)
    }

    DatasetsFormat(backendProfiles.parseJson.convertTo[Map[String, BackendProfile]]).read(datasets.parseJson)
  }
}
