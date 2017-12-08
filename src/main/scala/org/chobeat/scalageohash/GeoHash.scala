package org.chobeat.scalageohash

import ImplicitConversions._
import org.chobeat.scalageohash.GeoHash.BoxRange

class GeoHash(val geohashString: String) {
  lazy val boxRange: BoxRange = GeoHash.decodeGeohash(geohashString)

  /**
    * The geohash box represented as a list of points
    */
  lazy val boxPoints: Seq[GeoPoint] = {
    val ((latB, latE), (lonB, lonE)) = boxRange
    Seq(
      GeoPoint(latB, lonB),
      GeoPoint(latB, lonE),
      GeoPoint(latE, lonE),
      GeoPoint(latE, lonB),
    )
  }

  lazy val centroid: GeoPoint = GeoHash.boxMid(boxRange)

  override def equals(obj: scala.Any): Boolean = obj match {
    case s:String => s.equals(geohashString)
    case x:GeoHash=> x.geohashString.equals(geohashString)
    case _=>false

  }

  lazy val neighbors: NeighborsSet = NeighborsSet(geohashString)

  override def toString: String = geohashString

}

object GeoHash {
  // Inspired by the MIT licensed implementation by Chris Veness from:
  // http://www.movable-type.co.uk/scripts/geohash.html

  private val BASE_32 = "0123456789bcdefghjkmnpqrstuvwxyz"

  // describes a latitude or longitude range by its lower bound (left) and upper bound (right)
  type Range = (Double, Double)

  // describes a box by latitude (left) and longitude (right) range
  type BoxRange = (Range, Range)

  /**
    * Computes the middle value of a Range
    * @param range
    * @return
    */
  private def rangeMid(range: (Double, Double)): Double =
    (range._1 + range._2) / 2

  /**
    * Computes the center point of a BoxRange
    * @param boxRange
    * @return
    */
  private def boxMid(boxRange: BoxRange): GeoPoint = {
    val (latRange, lonRange) = boxRange
    GeoPoint(rangeMid(latRange), rangeMid(lonRange))
  }

  /**
    *  Splits a range given the value of the coordinate along which we want to split the range
    * @param x a coordinate of the point to encode
    * @param range the range to split
    * @param mid the middle value of the range to split
    * @return a split range
    */
  private def splitRangeByValue(x: Double,
                                range: (Double, Double),
                                mid: Double): (Double, Double) = {

    if (x >= mid) {
      (mid, range._2)
    } else {
      (range._1, mid)
    }
  }

  /**
    *  Splits a range given the bit used for decoding the current digit
    * @param bit
    * @param range the range to split
    * @return
    */
  private def splitRangeByBit(bit: Int, range: Range): Range = {
    val mid = rangeMid(range)
    if (bit > 0) (mid, range._2)
    else (range._1, mid)
  }

  private def getNewRangeAndIndex(point: GeoPoint,
                                  ranges: BoxRange,
                                  isEven: Boolean,
                                  base32CharIndex: Int): (BoxRange, Int) = {

    val (changingRange, staticRange): BoxRange =
      if (isEven) ranges.swap else ranges
    val changingCoord: Double =
      if (isEven) point.lon else point.lat

    val mid = rangeMid(changingRange)

    val newRanges =
      (splitRangeByValue(changingCoord, changingRange, mid), staticRange)

    val newIndex = (base32CharIndex << 1) | (if (changingCoord >= mid) 1
                                             else 0)

    val adjustedRange = if (isEven) newRanges.swap else newRanges

    (adjustedRange, newIndex)
  }

  def encodeGeohashRec(point: GeoPoint,
                       partialGeohash: String = "",
                       ranges: BoxRange,
                       geohashLength: Int,
                       isEven: Boolean,
                       bit: Int,
                       base32CharIndex: Int): String = {
    if (partialGeohash.length == geohashLength)
      partialGeohash
    else {
      val (newRanges, newIndex) =
        getNewRangeAndIndex(point, ranges, isEven, base32CharIndex)

      if (bit < 4)
        encodeGeohashRec(point,
                         partialGeohash,
                         newRanges,
                         geohashLength,
                         !isEven,
                         bit + 1,
                         newIndex)
      else
        encodeGeohashRec(point,
                         partialGeohash + BASE_32.charAt(newIndex),
                         newRanges,
                         geohashLength,
                         !isEven,
                         bit = 0,
                         base32CharIndex = 0)
    }

  }

  def apply(geohashString: String) = new GeoHash(geohashString)

  /**
    * Encodes a GeoPoint as a 32-bit geohash
    * @param point a point to encode
    * @param length precision to use for encoding
    * @return
    */
  def encodeGeohash(point: GeoPoint, length: Int): GeoHash = {
    val rangeLat: Range = (-90.0, 90.0)
    val rangeLon: Range = (-180.0, 180.0)
    val geohash: String = encodeGeohashRec(point,
                                           "",
                                           (rangeLat, rangeLon),
                                           isEven = true,
                                           bit = 0,
                                           base32CharIndex = 0,
                                           geohashLength = length)
    new GeoHash(geohash)
  }

  private def decodeGeohashRec(geoHash: String,
                               ranges: BoxRange,
                               isEven: Boolean): BoxRange = {
    val (latRange, lonRange) = ranges
    if (geoHash.length == 0)
      (latRange, lonRange)
    else {
      decodeGeohashRec(
        geoHash.tail,
        decodeDigit(BASE_32.indexOf(geoHash.head), ranges, isEven),
        !isEven)
    }
  }

  /**
    * Split a range given a digit in a geohash
    * @param index the index in base32 of the digit
    * @param ranges the current lat-lon ranges
    * @param isEven
    * @param bitStep
    * @return
    */
  protected def decodeDigit(index: Int,
                            ranges: BoxRange,
                            isEven: Boolean,
                            bitStep: Int = 4): BoxRange = {
    if (bitStep == -1)
      ranges
    else {
      val bitN = index >> bitStep & 1
      val (latRange, lonRange) = ranges
      val splitRange = if (isEven) {

        (latRange, splitRangeByBit(bitN, lonRange))

      } else {
        (splitRangeByBit(bitN, latRange), lonRange)
      }
      decodeDigit(index, splitRange, !isEven, bitStep - 1)
    }

  }

  /**
    * Decodes a geohash
    * @param geohash
    * @return the corresponding box represented as a lat range and lon range pair
    */
  def decodeGeohash(geohash: String): BoxRange = {
    val rangeLat: Range = (-90.0, 90.0)
    val rangeLon: Range = (-180.0, 180.0)

    decodeGeohashRec(geohash, (rangeLat, rangeLon), isEven = true)

  }

  val neighbor: Map[Direction, List[String]] = Map(
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

  def getNeighbour(geoHash: String, direction: Direction): GeoHash = {
    if (direction == Center)
      geoHash
    else {
      val last = geoHash.last
      val parentString = geoHash.reverse.tail.reverse
      val t = geoHash.length % 2

      val parent: GeoHash =
        if (border(direction)(t).contains(last) && parentString.geohashString.nonEmpty)
          getNeighbour(parentString, direction)
        else
          parentString

      val magicMapChar = neighbor(direction)(t)
      val neighbourLastDigit = BASE_32(magicMapChar indexOf last).toString

      parent + neighbourLastDigit
    }
  }
}
