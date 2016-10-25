package geotrellis.config.json.dataset

import java.time.ZonedDateTime

case class JValidationOptions(sampleScale: Double, resolutionThreshold: Double, tmpDir: String, tiffLocal: String, dateTime: Option[ZonedDateTime])
