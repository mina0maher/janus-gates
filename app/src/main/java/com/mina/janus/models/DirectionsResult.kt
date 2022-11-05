package com.mina.janus.models

data class DirectionsResult(
    val geocodedWaypoints: List<GeocodedWaypoint>?,
    val routes: List<Route>?
)