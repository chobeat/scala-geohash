package org.chobeat.scalageohash
import org.chobeat.scalageohash.GeoHash.divideRangeByValue

import scala.collection.mutable

case class GeoPoint(lat: Double, lon: Double) {
  override def equals(that: Any): Boolean = that match {
    case that: GeoPoint => lat.equals(that.lat) && lon.equals(that.lon)
    case _              => false
  }

}

object ImplicitConversions {
  implicit def point2Tuple(p: GeoPoint): (Double, Double) =
    (p.lat, p.lon)
}

object GeoHash {
  private val BASE_32 = "0123456789bcdefghjkmnpqrstuvwxyz"

  type Range = (Double, Double)
  type Ranges = (Range, Range)

  private def middle(range: (Double, Double)) = {
    val (lat, lon) = range
    (lat + lon) / 2

  }

  private def divideRangeByValue(x: Double,
                                 range: (Double, Double)): (Double, Double) = {
    val mid = middle(range)
    if (x >= mid) {
      (mid, range._2)
    } else {
      (range._1, mid)
    }
  }

  private def getNewRangeAndIndex(point: GeoPoint,
                                  ranges: Ranges,
                                  isEven: Boolean,
                                  base32CharIndex: Int): (Ranges, Int) = {
    val (changingRange, staticRange): Ranges =
      if (isEven) ranges.swap else ranges
    val changingCoord: Double =
      if (isEven) point.lon else point.lat

    val newRanges =
      (divideRangeByValue(changingCoord, changingRange), staticRange)
    val newIndex = (base32CharIndex << 1) | (if (changingCoord >= middle(
                                                   changingRange)) 1
                                             else 0)
    val adjustedRange = if (isEven) newRanges.swap else newRanges
    (adjustedRange, newIndex)
  }

  def encodeGeohashRec(point: GeoPoint,
                       partialGeohash: String = "",
                       ranges: Ranges,
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

  def encodeGeohash(p: GeoPoint, length: Int): String = {
    val rangeLat = (-90.0, 90.0)
    val rangeLon = (-180.0, 180.0)
    encodeGeohashRec(p,
                     "",
                     (rangeLat, rangeLon),
                     isEven = true,
                     bit = 0,
                     base32CharIndex = 0,
                     geohashLength = length)

  }

}
