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
}
