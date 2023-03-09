package com.mina.janus.models

data class StatusModel(
    val authenticated: Boolean?,
    val email: String?,
    val name: String?,
    val role: String?,
    val verified: Boolean?
)