package geotrellis.config.json.dataset

import org.joda.time.DateTime

case class JValidationOptions(extentSize: Double, resolutionThreshold: Double, tmpDir: String, tiffLocal: String, dateTime: Option[DateTime])
