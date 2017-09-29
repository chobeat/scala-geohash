package org.chobeat.scalageohash

import org.scalactic.TolerantNumerics
import org.scalatest.{FlatSpec, Matchers}
import ImplicitConversions._
import org.chobeat.scalageohash.GeoHash.Ranges
class GeohashTest extends FlatSpec with Matchers {

  "encodeGeohash" should "return a valid geohash for first cell" in {
    val p = GeoPoint(-90.0, -180.00)
    GeoHash.encodeGeohash(p, 8) should be("00000000")

  }
  it should "return a valid geohash for last cell" in {
    val p = GeoPoint(90.0, 180.00)
    GeoHash.encodeGeohash(p, 8) should be("zzzzzzzz")

  }
  it should "return a valid geohash for a random position" in {
    val p = GeoPoint(-23, -12)
    GeoHash.encodeGeohash(p, 12) should be("7ezmnuvz79pr")

  }

  "decodeGeoHash" should "return a valid GeoPoint for first cell geohash" in {
    val geohash = "00000000000000000000000"
    val ((latL,latR),(lonU,lonD)): Ranges= GeoHash.decodeGeohash(geohash)
    assert(latL === -90.0)
    assert(latR+-0.1 === -90.0)
    assert(lonU === -180.0)
    assert(lonD+-0.1 === -180.0)

  }
  it should "return a valid GeoPoint for 1-character geohash" in {
    val geohash = "0"
    val ((latL,latR),(lonU,lonD)): Ranges= GeoHash.decodeGeohash(geohash)
    assert(latL === -90.0)
    assert(latR === -45.0)
    assert(lonU === -180.0)
    assert(lonD === -135.0)


  }

  it should "return a valid GeoPoint for a given geohash" in {
    val geohash = "5667gf"
    val ((latL,latR),(lonU,lonD)): Ranges= GeoHash.decodeGeohash(geohash)
    assert(latL+-0.1 === -76.6)
    assert(latR+-0.1 === -76.7)
    assert(lonU+-1 === -30.0)
    assert(lonD+-1 === -30.0)

  }

  "decodeDigit" should "divide lon range left once" in {
    val ranges:Ranges = ((-90,90),(-180,180))
    val result = GeoHash.decodeDigit(0,ranges,true)

    result shouldBe ((-90,-45),(-180,-135))
  }
  it should "divide lon range right once" in {
    val ranges:Ranges = ((-90,90),(-180,180))
    val result = GeoHash.decodeDigit(1,ranges,true)

    result shouldBe ((-90,-45),(-135,-90))
  }
  it should "divide lat range up once and lon range left once" in {
    val ranges:Ranges = ((-90,90),(-180,180))
    val result = GeoHash.decodeDigit(2,ranges,true)

    result shouldBe ((-45,0),(-180,-135))
  }
  it should "divide lat range right once" in {
    val ranges:Ranges = ((-90,90),(-180,180))
    val result = GeoHash.decodeDigit(3,ranges,true)

    result shouldBe ((-45,0),(-135,-90))
  }

}
