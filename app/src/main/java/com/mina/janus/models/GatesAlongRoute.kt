package com.mina.janus.models

import com.google.gson.annotations.Expose

data class GatesAlongRoute(
    val id: Int?,
    val name: String?,
    val location: Location?,
    val address: String?,
    val prices: List<Price>?,
    val imageUrl:String?,
    @Expose(serialize = false, deserialize = false)
    var isChecked:Boolean?
)