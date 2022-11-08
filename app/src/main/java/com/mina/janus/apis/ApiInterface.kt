package com.mina.janus.apis

import com.mina.janus.models.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {
    @POST("auth/login")
    fun logIn(@Body userSignInModel: UserLoginModel): Call<UserRegisterModel>

    @POST("auth/register")
    fun register(@Body userRegisterModel: UserRegisterModel): Call<UserRegisterModel>

    @POST("directions")
    fun getDirections(@Body addressModel: AddressModel):Call<DirectionsModel>

    @GET("gates")
    fun getGates():Call<GatesModel>
}