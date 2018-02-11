package org.chobeat.scalageohash

import org.scalatest.{FlatSpec, Matchers}
import ImplicitConversions._

class NeighborsTest extends FlatSpec with Matchers {
  "NeighborsSet" should "return all the neighboring geohash" in {
    val set = NeighborsSet("xn76urwe1g9y")
    set.center.geoHash.geohashString shouldEqual "xn76urwe1g9y"
    set.east.geoHash.geohashString shouldEqual "xn76urwe1gdn"
    set.north.geoHash.geohashString shouldEqual "xn76urwe1g9z"
    set.northEast.geoHash.geohashString shouldEqual "xn76urwe1gdp"
    set.northWest.geoHash.geohashString shouldEqual "xn76urwe1g9x"
    set.south.geoHash.geohashString shouldEqual "xn76urwe1g9v"
    set.southEast.geoHash.geohashString shouldEqual "xn76urwe1gdj"
    set.southWest.geoHash.geohashString shouldEqual "xn76urwe1g9t"
    set.west.geoHash.geohashString shouldEqual "xn76urwe1g9w"

  }


  "GeoHash getNeighbour" should "return a valid neighbour" in {
    val directions: List[Direction] = List(North, South, East, West)
    val geoHash = "gbsuv"
    val neighbours = List("gbsvj", "gbsut", "gbsuy", "gbsuu")
    for {
      (direction, neighbour) <- directions zip neighbours
    } neighbour.shouldEqual(GeoHash
      .getNeighbour(geoHash, direction)
      .geohashString)

  }
  it should "the original geohash" in {
    val geohash = GeoHash("gbsuv")
    val neighbour = GeoHash.getNeighbour(geohash, North)

    geohash.shouldEqual(GeoHash.getNeighbour(neighbour, South))

  }

  "GeoHash getNeighbourSet" should "return the same Neighbourset as the case class constructor" in {

    val geohash = "xn76urwe1g9y"
    GeoHash.getNeighbourSet(geohash) shouldEqual NeighborsSet(geohash)

  }

}
