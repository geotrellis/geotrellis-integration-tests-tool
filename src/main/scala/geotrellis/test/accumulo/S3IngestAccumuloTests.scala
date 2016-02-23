package geotrellis.test.accumulo

import geotrellis.test.s3.S3Load
import geotrellis.util.S3Support

class S3IngestAccumuloTests extends AccumuloTests with S3Support with S3Load
