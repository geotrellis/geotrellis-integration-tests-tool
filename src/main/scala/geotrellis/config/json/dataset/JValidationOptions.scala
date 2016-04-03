package geotrellis.config.json.dataset

import org.joda.time.DateTime

case class JValidationOptions(validationExtentSize: Double, resolutionThreshold: Double, dateTime: Option[DateTime])
