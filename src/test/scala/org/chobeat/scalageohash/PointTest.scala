package org.chobeat.scalageohash

import org.scalatest.{FlatSpec, Matchers}

class PointTest extends FlatSpec with Matchers {

  "A Point" should "be implicitely converted to float tuple" in {
    import ImplicitConversions.point2Tuple
    val (x, y): (Double, Double) = GeoPoint(0.0, 1.0)

    x should be(0.0)
    y should be(1.0)

  }

}
