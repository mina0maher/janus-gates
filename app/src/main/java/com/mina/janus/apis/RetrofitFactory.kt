package com.mina.janus.apis

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    fun apiInterface(): ApiInterface {
        return Retrofit.Builder()
            .baseUrl("https://janus-gates.up.railway.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

}