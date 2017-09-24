package org.chobeat.scalageohash

import org.scalatest.{FlatSpec, Matchers}

class GeoHashTest extends FlatSpec with Matchers{

  "A Point" should "be implicitely converted to float tuple" in {
    import ImplicitConversions.point2Tuple
    val (x,y):(Float,Float)=GeoPoint(0.0f,1.0f)

    x should be (0.0f)
    y should be (1.0f)

  }


}