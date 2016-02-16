package geotrellis.test

import org.scalatest._

class MainSpec extends FunSpec with Matchers {
  describe("Main") {
    it("should have correct hello sentence") {
      "Hello GeoTrellis" should be ("Hello GeoTrellis")
    }
  }

}
