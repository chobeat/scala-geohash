package org.chobeat

package object scalageohash {

  object ImplicitConversions {
    implicit def point2Tuple(p: GeoPoint): (Double, Double) =
      (p.lat, p.lon)
    implicit def geohash2String(g: GeoHash): String = g.geohashString
  }

}
