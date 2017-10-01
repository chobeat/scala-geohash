package org.chobeat.scalageohash

case class GeoPoint(lat: Double, lon: Double) {
  override def equals(that: Any): Boolean = that match {
    case that: GeoPoint => lat.equals(that.lat) && lon.equals(that.lon)
    case _              => false
  }

}
