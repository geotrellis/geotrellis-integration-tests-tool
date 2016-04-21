package geotrellis.config.json.dataset

import org.joda.time.DateTime

case class JValidationOptions(sampleScale: Double, resolutionThreshold: Double, tmpDir: String, targetCRS: String, tiffLocal: String, dateTime: Option[DateTime])
