package geotrellis

import com.typesafe.config.{Config => TConfig}

package object config {
  def either(property: String, default: String)(cfg: TConfig): String = try {
    cfg.getString(property)
  } catch {
    case _: Exception => default
  }
}
