package org.chobeat.scalageohash
import ImplicitConversions._
import Directions._
case class Neighbour[T <: Direction](direction: Direction,sourceGeoHash: GeoHash){

  lazy val geoHash:GeoHash = direction.getNeighbour(sourceGeoHash)
}

/***
  * Represents the 8 neighbours of a given geohash in a type safe manner. It guarantees that all the neighbours
  * are present.
  * @param sourceGeohash
  */
case class NeighborsSet(sourceGeohash: GeoHash){

  val center: Neighbour[Center.type] = Neighbour(Center,sourceGeohash)
  val north: Neighbour[North.type] = Neighbour(North,sourceGeohash)
  val south: Neighbour[South.type] = Neighbour(South,sourceGeohash)
  val west: Neighbour[West.type] = Neighbour(West,sourceGeohash)
  val east: Neighbour[East.type] = Neighbour(East,sourceGeohash)
  val northEast: Neighbour[NorthEast.type] = Neighbour(NorthEast,sourceGeohash)
  val northWest: Neighbour[NorthWest.type] = Neighbour(NorthWest,sourceGeohash)
  val southEast: Neighbour[SouthEast.type] = Neighbour(SouthEast,sourceGeohash)
  val southWest: Neighbour[SouthWest.type] = Neighbour(SouthWest,sourceGeohash)
}