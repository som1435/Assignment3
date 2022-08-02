package com.example.pokemonapp.di

import com.example.pokemonapp.PokemonRepository
import com.example.pokemonapp.data.PokemonApi
import com.example.pokemonapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokemonApi(): PokemonApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonApi::class.java)
    }

    @Singleton
    @Provides
    fun providePokemonRepository(pokemonApi: PokemonApi): PokemonRepository =
        PokemonRepository(pokemonApi)
}