package com.mina.janus.models

data class Leg(
    val arrivalTime: Any?,
    val departureTime: Any?,
    val distance: Distance?,
    val duration: Duration?,
    val durationInTraffic: Any?,
    val endAddress: String?,
    val endLocation: EndLocation?,
    val startAddress: String?,
    val startLocation: StartLocation?,
    val steps: List<Step>?
)