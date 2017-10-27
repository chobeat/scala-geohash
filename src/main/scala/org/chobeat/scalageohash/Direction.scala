package org.chobeat.scalageohash
import ImplicitConversions._
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
  def getNeighbor(geoHash: GeoHash):GeoHash={
    directions.foldLeft(GeoHash.getNeighbour(geoHash,Center))((g,d)=>GeoHash.getNeighbour(g,d))
  }
}
case object Center extends Direction{}
case object North extends Direction{}
case object South extends Direction{}
case object West extends Direction{}
case object East extends Direction{}
case object NorthEast extends Direction{
  override val directions: List[Direction] = List(North,East)
}
case object NorthWest extends Direction{
  override val directions: List[Direction] = List(North,West)
}
case object SouthEast extends Direction{
  override val directions: List[Direction] = List(South,East)
}
case object SouthWest extends Direction{
  override val directions: List[Direction] = List(South,West)
}
