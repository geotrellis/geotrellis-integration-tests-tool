package geotrellis.tests

import geotrellis.spark.LayerId

object AccumuloMain extends App {
  val name = "s3_accumulo"
  //spatialIngest(name)
  //save(LayerId(name, 20))
  val test = new S3IngestAccumuloTests()
  //test.combineLayers(LayerId(name, 20))
  //println("finished")
}
