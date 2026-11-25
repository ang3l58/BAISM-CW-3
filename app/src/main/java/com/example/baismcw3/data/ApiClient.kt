package com.example.baismcw3.data

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    const val HOST = "rickandmortyapi.com"
    const val BASE_URL = "https://$HOST/api/"

    // SHA-256 of SubjectPublicKeyInfo, base64. Leaf key expires with the cert on 2027-02-14,
    // so the Google Trust Services WE1 intermediate (valid until 2029-02-20) is pinned as a backup.
    private val certificatePinner = CertificatePinner.Builder()
        .add(HOST, "sha256/4HQtdsbE14lqcEw5YHG4JVx5rHWfpCmS09cZ0yfVd4U=")
        .add(HOST, "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4=")
        .build()

    // Long read timeout so a request held by a MITM interceptor can be edited by hand.
    val okHttp: OkHttpClient = OkHttpClient.Builder()
        .certificatePinner(certificatePinner)
        .readTimeout(120, TimeUnit.SECONDS)
        .callTimeout(150, TimeUnit.SECONDS)
        .build()

    val api: RickAndMortyApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RickAndMortyApi::class.java)
}
