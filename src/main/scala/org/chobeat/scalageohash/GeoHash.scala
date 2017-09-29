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

  private def rangeMid(range: (Double, Double)) = {
    val (lat, lon) = range
    (lat + lon) / 2

  }

  private def divideRangeByValue(x: Double,
                                 range: (Double, Double),
                                 mid: Double): (Double, Double) = {

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

    val mid = rangeMid(changingRange)

    val newRanges =
      (divideRangeByValue(changingCoord, changingRange, mid), staticRange)
    val newIndex = (base32CharIndex << 1) | (if (changingCoord >= mid) 1
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

  private def divideRangeByBit(bit: Int, range: Range): Range = {
    val mid = rangeMid(range)
    if (bit > 0) (mid, range._2)
    else (range._1, mid)
  }

  private def decodeGeohashRec(geoHash: String,
                               ranges: Ranges,
                               isEven: Boolean): Ranges = {
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

  def decodeDigit(index: Int, ranges: Ranges, isEven: Boolean): Ranges = {
    var j = 4
    var r: Ranges = ranges
    var isEvenT=isEven
    while ({
      j >= 0
    }) {
      val bitN = index >> j & 1
      val (latRange, lonRange) = r
      r = if (isEvenT) {

        // longitude
        (latRange, divideRangeByBit(bitN, lonRange))

      } else {
        (divideRangeByBit(bitN, latRange), lonRange)
      }
      isEvenT = !isEvenT
      j=j-1
    }
    r
  }

  def decodeGeohash(geohash: String): Ranges = {
    val rangeLat = (-90.0, 90.0)
    val rangeLon = (-180.0, 180.0)

    decodeGeohashRec(geohash, (rangeLat, rangeLon), true)

  }
}
