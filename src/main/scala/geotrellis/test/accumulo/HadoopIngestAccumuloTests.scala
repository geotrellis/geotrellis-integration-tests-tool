package geotrellis.test.accumulo

import geotrellis.test.HadoopLoad
import geotrellis.util.{HadoopSupport, S3Support}

class HadoopIngestAccumuloTests extends AccumuloTests with HadoopSupport with S3Support with HadoopLoad
