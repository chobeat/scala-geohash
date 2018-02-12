package org.chobeat.scalageohash
import ImplicitConversions._
import Directions._
sealed trait Direction
{
  /*
  In this context, a direction can be a composition of multiple cardinal points. We use this list
  to store an ordered collection of directions that defines a new, composed direction. i.e. NorthWest is the
  composition of North and West. Diagonal movement or grid exploration are both valid use cases for this feature.
  This is done because the GeoHash.getNeighbour() algorithm is optimized to compute the neighbours only on
  North, South, East and West and others directions must be defined in terms of these basic directions.
   */
  val directions:List[Direction] = List(this)
  def getNeighbour(geoHash: GeoHash):GeoHash={
    directions.foldLeft(getNeighbourByDir(geoHash,Directions.Center))((g,d)=>getNeighbourByDir(g,d))
  }

  private def getNeighbourByDir(geoHash: String, direction: Direction): GeoHash = {
  if (direction == Center)
    geoHash
  else {
    val last = geoHash.last
    val parentString = geoHash.reverse.tail.reverse
    val t = geoHash.length % 2

    val parent: GeoHash =
      if (Direction.border(direction)(t).contains(last) && parentString.geohashString.nonEmpty)
        getNeighbourByDir(parentString, direction)
      else
        parentString

    val magicMapChar = Direction.neighbour(direction)(t)
    val neighbourLastDigit = Direction.BASE_32(magicMapChar indexOf last).toString

    parent + neighbourLastDigit
  }
}
}

object Direction{

  private val BASE_32 = "0123456789bcdefghjkmnpqrstuvwxyz"


  val neighbour: Map[Direction, List[String]] = Map(
    North -> List("p0r21436x8zb9dcf5h7kjnmqesgutwvy",
      "bc01fg45238967deuvhjyznpkmstqrwx"),
    South -> List("14365h7k9dcfesgujnmqp0r2twvyx8zb",
      "238967debc01fg45kmstqrwxuvhjyznp"),
    East -> List("bc01fg45238967deuvhjyznpkmstqrwx",
      "p0r21436x8zb9dcf5h7kjnmqesgutwvy"),
    West -> List("238967debc01fg45kmstqrwxuvhjyznp",
      "14365h7k9dcfesgujnmqp0r2twvyx8zb")
  )
  val border: Map[Direction, List[String]] = Map(
    North -> List("prxz", "bcfguvyz"),
    South -> List("028b", "0145hjnp"),
    East -> List("bcfguvyz", "prxz"),
    West -> List("0145hjnp", "028b")
  )
}

object Directions {

  case object Center extends Direction {}

  case object North extends Direction {}

  case object South extends Direction {}

  case object West extends Direction {}

  case object East extends Direction {}

  case object NorthEast extends Direction {
    override val directions: List[Direction] = List(North, East)
  }

  case object NorthWest extends Direction {
    override val directions: List[Direction] = List(North, West)
  }

  case object SouthEast extends Direction {
    override val directions: List[Direction] = List(South, East)
  }

  case object SouthWest extends Direction {
    override val directions: List[Direction] = List(South, West)
  }

}