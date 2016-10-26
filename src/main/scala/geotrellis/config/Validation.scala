package geotrellis.config

import java.time.ZonedDateTime

case class Validation(sampleScale: Double, resolutionThreshold: Double, tmpDir: String, tiffLocal: String, dateTime: Option[ZonedDateTime])
