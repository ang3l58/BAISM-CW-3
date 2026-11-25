package com.baism.cw3.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun listCharacters(@Query("page") page: Int = 1): CharactersResponse

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): Character
}
