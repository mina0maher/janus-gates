package com.mina.janus.models

import com.google.gson.annotations.Expose

data class CarModelItem(
    val id: Int?,
    val licensePlate: String?,
    val model: String?,
    val name: String?,
    val ownerId: Int?,
    val type: VehicleType?,
    @Expose(serialize = false, deserialize = false)
    var isChecked:Boolean?
)