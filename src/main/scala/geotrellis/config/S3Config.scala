package geotrellis.config

trait S3Config extends Config {
  //: (bucket -> prefix)
  def getS3Params(s: String): (String, String) = try {
    val split = s.split("\\/")
    split(0) -> split(1)
  } catch {
    case _: Exception => throw new Exception("bad s3 path string")
  }
}
