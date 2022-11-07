package com.mina.janus.models

data class GatesAlongRoute(
    val address: String?,
    val id: Int?,
    val location: Location?,
    val name: String?,
    val prices: List<Price>?,
    val imageUrl:String?,
    var isChecked:Boolean?
)