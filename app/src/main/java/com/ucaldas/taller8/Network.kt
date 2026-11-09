package com.ucaldas.taller8

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class Personaje(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val image: String
)

data class PersonajesResponse(val results: List<Personaje>)

// Interfaz API
interface RickMortyApi {
    @GET("character")
    suspend fun obtenerPersonajes(): PersonajesResponse

    @GET("character/{id}")
    suspend fun obtenerPersonaje(@Path("id") id: Int): Personaje
}

// Instancia Singleton de Retrofit
object RetrofitInstance {
    val api: RickMortyApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RickMortyApi::class.java)
    }
}
