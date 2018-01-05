package org.chobeat.scalageohash

import org.scalatest.{FlatSpec, Matchers}
import org.chobeat.scalageohash.GeoHash.BoxRange
import ImplicitConversions._
class GeohashTest extends FlatSpec with Matchers {

  "encodeGeohash" should "return a valid geohash for first cell" in {
    val p = GeoPoint(-90.0, -180.00)
    val g: GeoHash = GeoHash.encodeGeohash(p, 8)
    g.geohashString should be("00000000")

  }
  it should "return a valid geohash for last cell" in {
    val p = GeoPoint(90.0, 180.00)
    val g: GeoHash = GeoHash.encodeGeohash(p, 8)
    g.geohashString should be("zzzzzzzz")

  }
  it should "return a valid geohash for a random position" in {
    val p = GeoPoint(-23, -12)
    val g: GeoHash = GeoHash.encodeGeohash(p, 12)
    g.geohashString should be("7ezmnuvz79pr")

  }
  it should "accept lat and lon values too" in {
    val g: GeoHash = GeoHash.encodeGeohash(-23, -12, 12)
    g.geohashString should be("7ezmnuvz79pr")

  }


  "decodeGeoHash" should "return a valid GeoPoint for first cell geohash" in {
    val geohash = "00000000000000000000000"
    val ((latL, latR), (lonU, lonD)): BoxRange = GeoHash.decodeGeohash(geohash)
    assert(latL === -90.0)
    assert(latR +- 0.1 === -90.0)
    assert(lonU === -180.0)
    assert(lonD +- 0.1 === -180.0)

  }
  it should "return a valid GeoPoint for 1-character geohash" in {
    val geohash = "0"
    val ((latL, latR), (lonU, lonD)): BoxRange = GeoHash.decodeGeohash(geohash)
    assert(latL === -90.0)
    assert(latR === -45.0)
    assert(lonU === -180.0)
    assert(lonD === -135.0)

  }

  it should "return a valid GeoPoint for a given geohash" in {
    val geohash = "5667gf"
    val ((latL, latR), (lonU, lonD)): BoxRange = GeoHash.decodeGeohash(geohash)
    assert(latL +- 0.1 === -76.6)
    assert(latR +- 0.1 === -76.7)
    assert(lonU +- 1 === -30.0)
    assert(lonD +- 1 === -30.0)

  }

  "GeoHash class" should "be implicitely converted to a string" in {
    val g: String = new GeoHash("0123")
    g shouldBe "0123"

  }

  it should "be equal to another geohash" in {
    val g1 = GeoHash("abcde")
    val g2 = GeoHash("abcde")

    g1.shouldEqual(g2)

  }

  it should "be equal to string" in {
    val g1 = GeoHash("abcde")
    val s = "abcde"

    g1.shouldEqual(s)

  }

  "GeoHash centroid" should "be the center point of the box corresponding to a geohash" in {
    val geohash = GeoHash("5667gf")
    val (lat, lon): (Double, Double) = geohash.centroid
    assert(lat === -76.67083740 +- 1)
    assert(lon === -30.41564941 +- 1)

  }

  "Geohash getNeighbours" should "return a valid neighbour" in {
    val directions: List[Direction] = List(North, South, East, West)
    val geoHash = "gbsuv"
    val neighbours = List("gbsvj", "gbsut", "gbsuy", "gbsuu" )
    for {
      (direction, neighbour) <- directions zip neighbours
    } neighbour.shouldEqual(GeoHash
      .getNeighbour(geoHash, direction)
      .geohashString)

  }
  it should "the original geohash" in {
    val geohash = GeoHash("gbsuv")
    val neighbour=GeoHash.getNeighbour(geohash,North)

    geohash.shouldEqual(GeoHash.getNeighbour(neighbour,South))

  }


}
