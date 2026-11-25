package com.baism.cw3.data

import com.baism.cw3.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://rickandmortyapi.com/api/"
    private const val HOST = "rickandmortyapi.com"

    /**
     * SPKI SHA-256 pin for rickandmortyapi.com leaf certificate.
     * Obtained with openssl / cryptography (see report):
     *   sha256/4HQtdsbE14lqcEw5YHG4JVx5rHWfpCmS09cZ0yfVd4U=
     */
    private const val SPKI_PIN = "sha256/4HQtdsbE14lqcEw5YHG4JVx5rHWfpCmS09cZ0yfVd4U="

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)

        if (BuildConfig.ENABLE_CERT_PINNING) {
            val pinner = CertificatePinner.Builder()
                .add(HOST, SPKI_PIN)
                .build()
            builder.certificatePinner(pinner)
        }

        builder.build()
    }

    val api: RickAndMortyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RickAndMortyApi::class.java)
    }
}
