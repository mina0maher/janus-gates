package com.mina.janus.apis

import com.mina.janus.models.AddressModel
import com.mina.janus.models.DirectionsModel
import com.mina.janus.models.UserLoginModel
import com.mina.janus.models.UserRegisterModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("auth/login")
    fun logIn(@Body userSignInModel: UserLoginModel): Call<UserRegisterModel>

    @POST("auth/register")
    fun register(@Body userRegisterModel: UserRegisterModel): Call<UserRegisterModel>

    @POST("directions")
    fun getDirections(@Body addressModel: AddressModel):Call<DirectionsModel>
}