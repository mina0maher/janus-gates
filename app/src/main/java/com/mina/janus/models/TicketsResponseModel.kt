package com.mina.janus.models

data class TicketsResponseModel(
    val gate: GatesAlongRoute?,
    val id: Int?,
    val licensePlate: String?,
    val ownerId: Int?,
    val ownerName: String?,
    val paidPrice: Double?,
    val timeStamp: Long?,
    val vehicleType: VehicleType?
)