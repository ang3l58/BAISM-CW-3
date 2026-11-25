package com.example.baismcw3.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    const val BASE_URL = "https://rickandmortyapi.com/api/"

    val okHttp: OkHttpClient = OkHttpClient.Builder().build()

    val api: RickAndMortyApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RickAndMortyApi::class.java)
}
