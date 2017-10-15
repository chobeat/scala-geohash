package org.chobeat.scalageohash

sealed trait Direction
case object Center extends Direction
case object North extends Direction
case object South extends Direction
case object West extends Direction
case object East extends Direction
case object NorthEast extends Direction
case object NorthWest extends Direction
case object SouthEast extends Direction
case object SouthWest extends Direction

case class Neighbour[T<:Direction](direction:Direction)(geoHash: GeoHash)

case class NeighborsSet(center: Neighbour[Center.type],
                        north: Neighbour[North.type],
                        south: Neighbour[South.type],
                        west: Neighbour[West.type],
                        east: Neighbour[East.type],
                        northEast: Neighbour[NorthEast.type],
                        northWest: Neighbour[NorthWest.type],
                        southEast: Neighbour[SouthEast.type],
                        southWest: Neighbour[SouthWest.type])
