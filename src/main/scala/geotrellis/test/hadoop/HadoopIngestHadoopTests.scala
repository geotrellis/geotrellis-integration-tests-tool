package geotrellis.test.hadoop

import geotrellis.test.HadoopLoad
import geotrellis.util.{HadoopSupport, S3Support}

class HadoopIngestHadoopTests extends HadoopTests with HadoopSupport with S3Support with HadoopLoad
