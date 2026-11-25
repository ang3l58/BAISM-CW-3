package com.baism.cw3.data

data class CharactersResponse(
    val info: PageInfo,
    val results: List<Character>
)

data class PageInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: NamedResource,
    val location: NamedResource,
    val image: String,
    val url: String,
    val created: String
)

data class NamedResource(
    val name: String,
    val url: String
)
