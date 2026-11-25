package com.example.baismcw3.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class PageInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?,
)

data class NamedLink(
    val name: String,
    val url: String,
)

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: NamedLink,
    val location: NamedLink,
    val image: String,
    val episode: List<String>,
    val created: String,
)

data class CharacterPage(
    val info: PageInfo,
    val results: List<Character>,
)

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): CharacterPage

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): Character
}
