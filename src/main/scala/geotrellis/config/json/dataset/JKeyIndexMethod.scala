package geotrellis.config.json.dataset

import geotrellis.spark.io.index._

case class JKeyIndexMethod(`type`: String, timeTag: Option[String] = None, timeFormat: Option[String] = None, temporalResolution: Option[Long] = None) {
  private def _getKeyIndexMethod: KeyIndexMethod[_] = (`type`, temporalResolution) match {
    case ("rowmajor", None)    => RowMajorKeyIndexMethod
    case ("hilbert", None)     => HilbertKeyIndexMethod
    case ("hilbert", Some(tr)) => HilbertKeyIndexMethod(tr.toInt)
    case ("zorder", None)      => ZCurveKeyIndexMethod
    case ("zorder", Some(tr))  => ZCurveKeyIndexMethod.byMilliseconds(tr)
    case _                     => throw new Exception("unsupported keyIndexMethod definition")
  }

  def getKeyIndexMethod[K] = _getKeyIndexMethod.asInstanceOf[KeyIndexMethod[K]]
}
