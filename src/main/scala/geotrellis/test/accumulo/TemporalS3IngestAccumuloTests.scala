package geotrellis.test.accumulo

import geotrellis.test.s3.TemporalS3Load
import geotrellis.util.S3Support

class TemporalS3IngestAccumuloTests extends TemporalAccumuloTests with S3Support with TemporalS3Load
