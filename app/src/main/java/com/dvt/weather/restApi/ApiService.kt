package com.dvt.weather.restApi

import android.provider.Settings.Global.getString
import com.dvt.weather.R
import com.dvt.weather.restApi.CurrentWeather.CurrentWeatherResponse
import com.dvt.weather.restApi.ForecastWeather.ForecastModel
import com.dvt.weather.utils.Constants.Companion.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*


interface ApiService {


    // @GET("placeholder/user/{userId}")
    //suspend fun getUser(@Path("userId") userId:String): User
    @GET("data/2.5/weather")
    suspend fun getCurrent(
        @Query("q") location:String,
        @Query("units")units: String ="metric",
        @Query("appid")appid: String = API_KEY
    ): CurrentWeatherResponse
    //http://api.openweathermap.org/data/2.5/forecast?lat=-26.2&lon=28.04&appid=636e6df4d5c9c1d14640e9ff8a629364
    //Forecast weather
    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("q") location:String,
        @Query("units")units: String="metric",
        @Query("appid")appid: String = API_KEY
    ): ForecastModel


}