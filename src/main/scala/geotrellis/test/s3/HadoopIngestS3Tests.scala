package geotrellis.test.s3

import geotrellis.test.HadoopLoad
import geotrellis.util.{HadoopSupport, S3Support}

class HadoopIngestS3Tests extends S3Tests with HadoopSupport with S3Support with HadoopLoad
