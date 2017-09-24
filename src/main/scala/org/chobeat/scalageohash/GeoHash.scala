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

  def encodeGeohashRec(point: GeoPoint,
                       partialGeohash: String = "",
                       rangeLat: (Double, Double),
                       rangeLon: (Double, Double),
                       geohashLength: Int,
                       isEven: Boolean,
                       bit: Int,
                       base32CharIndex: Int): String = {
    if (partialGeohash.length == geohashLength)
      partialGeohash
    else {
      val (newRangeLat, newRangeLon, newIndex) =
        if (isEven) {
          val mid = middle(rangeLon)
          val newRangeLon = divideRangeByValue(point.lon, rangeLon)
          val newRangeLat = rangeLat
          val newIndex = (base32CharIndex << 1) | (if (point.lon >= mid) 1
                                                   else 0)
          (newRangeLat, newRangeLon, newIndex)
        } else {
          val mid = middle(rangeLat)
          val newRangeLat = divideRangeByValue(point.lat, rangeLat)
          val newRangeLon = rangeLon
          val newIndex = (base32CharIndex << 1) | (if (point.lat >= mid) 1
                                                   else 0)
          (newRangeLat, newRangeLon, newIndex)
        }
      if (bit < 4)
        encodeGeohashRec(point,
                         partialGeohash,
                         newRangeLat,
                         newRangeLon,
                         geohashLength,
                         !isEven,
                         bit + 1,
                         newIndex)
      else
        encodeGeohashRec(point,
                         partialGeohash + BASE_32.charAt(newIndex),
                         newRangeLat,
                         newRangeLon,
                         geohashLength,
                         !isEven,
                         bit = 0,
                         base32CharIndex = 0)
    }

  }

  def encodeGeohash(p: GeoPoint, length: Int): String = {
    val latRange = (-90.0, 90.0)
    val lonRange = (-180.0, 180.0)
    encodeGeohashRec(p,
                     "",
                     latRange,
                     lonRange,
                     isEven = true,
                     bit = 0,
                     base32CharIndex = 0,
                     geohashLength = length)

  }

}
