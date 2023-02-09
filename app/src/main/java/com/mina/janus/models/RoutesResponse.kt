package com.mina.janus.models

data class RoutesResponse(
    val description: String?,
    val distanceMeters: Int?,
    val duration: String?,
    val legs: List<Any>?,
    val polyline: Polyline?,
    val routeLabels: List<Any>?,
    val routeToken: String?,
    val warnings: List<Any>?
)