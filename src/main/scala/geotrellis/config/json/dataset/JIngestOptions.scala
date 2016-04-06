package geotrellis.config.json.dataset

import geotrellis.raster.resample._

case class JIngestOptions(resampleMethod: PointResampleMethod, layoutScheme: JLayoutScheme, keyIndexMethod: JKeyIndexMethod)
