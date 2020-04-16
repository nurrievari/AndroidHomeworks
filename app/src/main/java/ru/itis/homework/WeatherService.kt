package ru.itis.homework

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather")
    suspend fun weatherByName(@Query("q") name: String): WeatherResponse

    @GET("weather")
    suspend fun weatherById(@Query("id") id: Int): WeatherResponse

    @GET("find")
    suspend fun weatherCitiesInCycle(@Query("lat") lat: Double,
                                     @Query("lon") lon: Double,
                                     @Query("cnt") cnt: Int): CircleWeatherResponse
}
