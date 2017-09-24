package org.chobeat.scalageohash

import org.scalatest.{FlatSpec, Matchers}

class GeohashTest extends FlatSpec with Matchers {

  "encodeGeohash" should "return a valid geohash for first cell" in {
    val p = GeoPoint(-90.0,-180.00)
    GeoHash.encodeGeohash(p,8) should be ("00000000")

  }
  it should "return a valid geohash for last cell" in {
    val p = GeoPoint(90.0,180.00)
    GeoHash.encodeGeohash(p,8) should be ("zzzzzzzz")

  }
  it should "return a valid geohash for a random position" in {
    val p = GeoPoint(-23,-12)
    GeoHash.encodeGeohash(p,12) should be ("7ezmnuvz79pr")

  }
}
