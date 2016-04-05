package geotrellis.config

import geotrellis.config.json.backend.JCredensials
import geotrellis.config.json.dataset.JConfig

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import java.io.InputStream

trait Config {
  lazy val backendCfg = """{
                     |  "accumulo": [{
                     |    "name": "gis",
                     |    "instance": "gis",
                     |    "zookeepers": "localhost",
                     |    "user": "root",
                     |    "password": "secret"
                     |  }],
                     |  "s3": [],
                     |  "hadoop": []
                     |}""".stripMargin

  lazy val datasetCfg = """[{
                              |   "name":"nex",
                              |   "type":{
                              |      "ingestCredensials": "gis",
                              |      "loadBackend":"hadoop",
                              |      "ingestBackend":"accumulo",
                              |      "tileType":"singleband",
                              |      "ingestType":"spatial"
                              |   },
                              |   "path":{
                              |      "load":"/geotrellis-integration-load/",
                              |      "ingest":"gtintegration"
                              |   },
                              |   "ingestOptions":{
                              |      "resampleMethod":"bilinear",
                              |      "layoutScheme":{
                              |         "type":"zoomed",
                              |         "crs":"EPSG:3857",
                              |         "tileSize":256,
                              |         "resolutionThreshold":0.1
                              |      },
                              |      "keyIndexMethod":{
                              |         "type":"zorder"
                              |      }
                              |   },
                              |   "validationOptions":{
                              |      "extentSize":0.3,
                              |      "resolutionThreshold":0.1,
                              |      "tmpDir": "/data/tmp/",
                              |      "tiffLocal": "/data/tmp/tasmax_amon_BCSD_rcp26_r1i1p1_CONUS_CCSM4_200601-201012-200601120000_0_0.tif"
                              |   }
                              |},
                              |{
                              |   "name":"nex",
                              |   "type":{
                              |      "ingestCredensials": "gis",
                              |      "loadBackend":"hadoop",
                              |      "ingestBackend":"accumulo",
                              |      "tileType":"singleband",
                              |      "ingestType":"temporal"
                              |   },
                              |   "path":{
                              |      "load":"/geotrellis-integration-load/",
                              |      "ingest":"gtintegration"
                              |   },
                              |   "ingestOptions":{
                              |      "resampleMethod":"bilinear",
                              |      "layoutScheme":{
                              |         "type":"zoomed",
                              |         "crs":"EPSG:3857",
                              |         "tileSize":256,
                              |         "resolutionThreshold":0.1
                              |      },
                              |      "keyIndexMethod":{
                              |         "type":"zorder",
                              |         "timeTag":"ISO_TIME",
                              |         "timeFormat":"yyyy-MM-dd'T'HH:mm:ss",
                              |         "temporalResolution":1314000000
                              |      }
                              |   },
                              |   "validationOptions":{
                              |      "extentSize":0.3,
                              |      "resolutionThreshold":0.1,
                              |      "dateTime": "2006-01-16T12:00:00",
                              |      "tmpDir": "/data/tmp/",
                              |      "tiffLocal": "/data/tmp/tasmax_amon_BCSD_rcp26_r1i1p1_CONUS_CCSM4_200601-201012-200601120000_0_0.tif"
                              |   }
                              |}]""".stripMargin

  lazy val credensials  = JCredensials.read(backendCfg)
  lazy val dataset      = JConfig.readList(datasetCfg)
  lazy val splitDataset = Config.splitConfig(dataset)
}

object Config extends Config {
  private def getJson(stream: InputStream): String = scala.io.Source.fromInputStream(stream).getLines.mkString(" ")

  def getJson(filePath: String, conf: Configuration): String = {
    val path = new Path(filePath)
    val fs = FileSystem.get(conf)
    val is = fs.open(path)
    val json = getJson(is)
    is.close(); fs.close(); json
  }

  def getJson(resource: String): String = {
    val stream: InputStream = getClass.getResourceAsStream(resource)
    val json = getJson(stream)
    stream.close(); json
  }

  // cfgs => (ss, sm, ts, tm)
  def splitConfig(cfgs: List[JConfig]): (List[JConfig], List[JConfig], List[JConfig], List[JConfig]) =
    (cfgs.filter(c => c.isSpatial && c.isSingleband),
     cfgs.filter(c => c.isSpatial && c.isMultiband),
     cfgs.filter(c => c.isTemporal && c.isSingleband),
     cfgs.filter(c => c.isTemporal && c.isMultiband))
}
