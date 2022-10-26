package com.mina.janus.apis

import com.mina.janus.models.UserLoginModel
import com.mina.janus.models.UserRegisterModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("login")
    fun logIn(@Body userSignInModel: UserLoginModel): Call<UserRegisterModel>

    @POST("register")
    fun register(@Body userRegisterModel: UserRegisterModel): Call<UserRegisterModel>

}