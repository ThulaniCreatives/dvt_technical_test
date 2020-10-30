package com.dvt.weather.restApi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherRetrofitBuilder {
    //http://api.openweathermap.org/data/2.5/weather?q=johannesburg&units=metric&appid=636e6df4d5c9c1d14640e9ff8a629364
    const val baseURL = "https://open-api.xyz/"
    const val baseURL2 = "https://api.openweathermap.org/"

    val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
                .baseUrl(baseURL2)
                .addConverterFactory(GsonConverterFactory.create())

    }
    val apiService:ApiService by lazy {
        retrofitBuilder.build().create(ApiService::class.java)
    }


}