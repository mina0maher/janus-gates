package com.mina.janus.models

data class DirectionsModel(
    val directionsResult: DirectionsResult?,
    val gatesAlongRoute: List<GatesAlongRoute>?
)