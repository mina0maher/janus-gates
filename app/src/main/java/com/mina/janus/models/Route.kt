package com.mina.janus.models

data class Route(
    val bounds: Bounds?,
    val copyrights: String?,
    val fare: Any?,
    val legs: List<Leg>?,
    val overviewPolyline: OverviewPolyline?,
    val summary: String?,
    val warnings: List<Any?>,
    val waypointOrder: List<Any>?
)