package org.chobeat.scalageohash

case class Neighbour[T <: Direction](direction: Direction)(geoHash: GeoHash)

case class NeighborsSet(center: Neighbour[Center.type],
                        north: Neighbour[North.type],
                        south: Neighbour[South.type],
                        west: Neighbour[West.type],
                        east: Neighbour[East.type],
                        northEast: Neighbour[NorthEast.type],
                        northWest: Neighbour[NorthWest.type],
                        southEast: Neighbour[SouthEast.type],
                        southWest: Neighbour[SouthWest.type])

object NeighborsSet {
  def apply(geohash: GeoHash):NeighborsSet = {
    NeighborsSet(
      Neighbour(Center)(geohash),
      Neighbour(North)(geohash),
      Neighbour(South)(geohash),
      Neighbour(West)(geohash),
      Neighbour(East)(geohash),
      Neighbour(NorthEast)(geohash),
      Neighbour(NorthWest)(geohash),
      Neighbour(SouthEast)(geohash),
      Neighbour(SouthWest)(geohash)
    )
  }
}
