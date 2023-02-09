package com.mina.janus.apis

import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.*


object RetrofitFactory {
    fun apiInterface(): ApiInterface {

        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        val cookieJar: CookieJar = JavaNetCookieJar(cookieManager)
        val builder = OkHttpClient.Builder()
        builder.cookieJar(cookieJar)
        val client = builder.build()

        return Retrofit.Builder()
            .baseUrl("https://janus.fly.dev/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .client(OkHttpClient().newBuilder().cookieJar(SessionCookieJar()).build())
            .build()
            .create(ApiInterface::class.java)
    }

    private class SessionCookieJar : CookieJar {
        private lateinit var cookies: List<Cookie>
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            if (url.encodedPath().endsWith("login")&&url.encodedPath().endsWith("register")) {
                this.cookies = ArrayList(cookies)
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return if (!url.encodedPath().endsWith("login")&&!url.encodedPath().endsWith("register") && ::cookies.isInitialized) {
                cookies
            } else Collections.emptyList()
        }
    }
}