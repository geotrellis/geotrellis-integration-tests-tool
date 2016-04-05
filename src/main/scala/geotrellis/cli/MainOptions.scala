package geotrellis.cli

import scopt.OptionParser

object MainOptions {
  val parser = new OptionParser[MainArgs](Info.name) {
    head(Info.name, Info.version)

    opt[String]("datasets") action { (x, c) =>
      c.copy(datasets = x)
    } validate { x =>
      if (x.nonEmpty) success else failure(s"Option --datasets must be non-empty")
    } text s"datasets is a non-empty String property"

    opt[String]("credentials") action { (x, c) =>
      c.copy(credentials = x)
    } validate { x =>
      if (x.nonEmpty) success else failure(s"Option --credentials must be non-empty")
    } text s"credentials is a non-empty String property"

    help("help") text "prints this usage text"
  }

  def parse(args: Array[String]) = parser.parse(args, MainArgs())
}
