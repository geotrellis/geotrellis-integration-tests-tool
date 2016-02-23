package geotrellis.test.hadoop

import geotrellis.test.s3.S3Load
import geotrellis.util.S3Support

class S3IngestHadoopTests extends HadoopTests with S3Support with S3Load
