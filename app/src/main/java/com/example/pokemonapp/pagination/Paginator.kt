package com.example.pokemonapp.pagination

interface Paginator<Key, Item> {
    suspend fun loadNextItems()
}