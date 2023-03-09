package com.mina.janus.models

data class VehicleType(
    val id: Int,
    val name: String,
    val imageUrl: String
){
    override fun toString(): String = name
}