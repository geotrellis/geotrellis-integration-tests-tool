package geotrellis.util

object Colors {
  val black   = "\u001b[0;30m"
  val red     = "\u001b[0;31m"
  val green   = "\u001b[0;32m"
  val yellow  = "\u001b[0;33m"
  val blue    = "\u001b[0;34m"
  val magenta = "\u001b[0;35m"
  val cyan    = "\u001b[0;36m"
  val grey    = "\u001b[0;37m"
  val default = "\u001b[m"

  private def _colorLine(color: String, str: String): String = s"${color}${str}${default}"

  def black(str: String): String   = _colorLine(black, str)
  def red(str: String): String     = _colorLine(red, str)
  def green(str: String): String   = _colorLine(green, str)
  def yellow(str: String): String  = _colorLine(yellow, str)
  def blue(str: String): String    = _colorLine(blue, str)
  def magenta(str: String): String = _colorLine(magenta, str)
  def cyan(str: String): String    = _colorLine(cyan, str)
  def grey(str: String): String    = _colorLine(grey, str)
}
