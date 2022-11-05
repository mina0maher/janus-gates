package com.mina.janus.models

data class Step(
    val distance: Distance?,
    val htmlInstructions: String?,
    val maneuver: String?,
    val polyline: Polyline?,
    val steps: Any?,
    val transitDetails: Any?,
    val travelMode: String?
)