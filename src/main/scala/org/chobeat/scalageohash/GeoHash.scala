package org.chobeat.scalageohash

case class GeoPoint(lat: Float, lon: Float) {
  override def equals(that: Any):Boolean = that match{
    case that: GeoPoint => lat.equals(that.lat) && lon.equals(that.lon)
    case _           => false
  }


}

object ImplicitConversions {
  implicit def point2Tuple(p: GeoPoint): (Float, Float) =
    (p.lat, p.lon)
}

object GeoHash {


}
