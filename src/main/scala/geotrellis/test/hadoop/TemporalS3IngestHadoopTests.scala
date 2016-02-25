package geotrellis.test.hadoop

import geotrellis.test.s3.TemporalS3Load
import geotrellis.util.S3Support

class TemporalS3IngestHadoopTests extends TemporalHadoopTests with S3Support with TemporalS3Load
