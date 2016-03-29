package geotrellis

import com.typesafe.config.{Config => TConfig}

import scala.collection.JavaConversions._

package object config {
  def either(property: String, default: String = "")(cfg: TConfig): String = try {
    cfg.getString(property)
  } catch {
    case _: Exception => default
  }

  def eitherConfigList(property: String, default: List[TConfig] = List())(cfg: TConfig): List[TConfig] = try {
    cfg.getConfigList(property).toList
  } catch {
    case _: Exception => default
  }
}
