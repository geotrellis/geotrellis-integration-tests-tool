package geotrellis

import com.typesafe.config.{Config => TConfig}

import scala.collection.JavaConversions._

package object config {
  def either(property: String, default: String = "")(cfg: TConfig): String = try {
    cfg.getString(property)
  } catch {
    case _: Exception => default
  }

  def eitherInt(property: String, default: Int = 0)(cfg: TConfig): Int = try {
    cfg.getInt(property)
  } catch {
    case _: Exception => default
  }

  def optionLong(property: String)(cfg: TConfig): Option[Long] = try {
    Some(cfg.getLong(property))
  } catch {
    case _: Exception => None
  }

  def eitherDouble(property: String, default: Double = 0)(cfg: TConfig): Double = try {
    cfg.getDouble(property)
  } catch {
    case _: Exception => default
  }

  def eitherConfigList(property: String, default: List[TConfig] = List())(cfg: TConfig): List[TConfig] = try {
    cfg.getConfigList(property).toList
  } catch {
    case _: Exception => default
  }
}
