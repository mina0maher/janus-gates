package com.mina.janus.models

data class GeocodedWaypoint(
    val geocoderStatus: String?,
    val partialMatch: Boolean?,
    val placeId: String?,
    val types: List<String>?
)