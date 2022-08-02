package com.example.pokemonapp

import com.example.pokemonapp.data.Pokemon
import com.example.pokemonapp.data.PokemonApi
import com.example.pokemonapp.utils.Constants
import javax.inject.Inject
import kotlin.Result

class PokemonRepository @Inject constructor(private val pokemonApi: PokemonApi) {

    suspend fun getPokemonList(page: Int, pageSize: Int): Result<List<Pokemon>> {
        val response = try {
            val offset = page * pageSize
            val limit = pageSize
            if (offset + limit <= Constants.PAGINATION_MAX_LIMIT)
                pokemonApi.getPokemonList(limit, offset).results
            else
                throw RateLimitException("Limit Reached!")
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return Result.success(response)
    }
}
