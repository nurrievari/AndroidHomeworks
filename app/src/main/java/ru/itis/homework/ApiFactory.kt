package ru.itis.homework

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    private val authInterceptor = Interceptor { chain ->
        // better use separate classes for Interceptors
        val newUrl = chain.request().url().newBuilder()
            .addQueryParameter("apiKey", BuildConfig.API_KEY)
            .build()

        val newRequest = chain.request().newBuilder().url(newUrl).build()
        chain.proceed(newRequest)
    }

    private val unitsInterceptor = Interceptor { chain ->
        // better use separate classes for Interceptors
        val newUrl = chain.request().url().newBuilder()
            .addQueryParameter("units", "metric")
            .build()

        val newRequest = chain.request().newBuilder().url(newUrl).build()
        chain.proceed(newRequest)
    }

    private val client by lazy {
        OkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .addInterceptor(unitsInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherService: WeatherService by lazy { retrofit.create(WeatherService::class.java) }
}
