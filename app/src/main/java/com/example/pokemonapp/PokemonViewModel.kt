package com.example.pokemonapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.data.Pokemon
import com.example.pokemonapp.pagination.CustomPaginator
import com.example.pokemonapp.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(private val pokemonRepository: PokemonRepository) :
    ViewModel() {

    var state by mutableStateOf(ScreenState())

    private val paginator = CustomPaginator(
        initialKey = state.page,
        onLoadUpdated = {
            state = state.copy(isLoading = it)
        },
        onRequest = { nextPage: Int ->
            pokemonRepository.getPokemonList(nextPage, Constants.PAGE_SIZE)
        },
        getNextKey = {
            state.page + 1
        },
        onError = {
            when (it) {
                is RateLimitException -> {
                    state = state.copy(error = it.message, endReached = true)
                }
                else -> {
                    state = state.copy(error = "Unable to get Pokemon")
                }
            }
        },
        onSuccess = { items: List<Pokemon>, newKey ->
            val pokemonList = items.map { pokemon ->
                val pokemonId = if (pokemon.url.endsWith("/")) {
                    pokemon.url.dropLast(1).takeLastWhile { it.isDigit() }
                } else {
                    pokemon.url.takeLastWhile { it.isDigit() }
                }
                val imageUrl =
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemonId}.png"
                Pokemon(pokemon.name, imageUrl)
            }
            state = state.copy(
                items = state.items + pokemonList,
                page = newKey,
                endReached = items.isEmpty()
            )
        }
    )

    init {
        loadNextItems()
    }

    fun loadNextItems() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }
}

data class ScreenState(
    val isLoading: Boolean = false,
    val items: List<Pokemon> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Int = 0
)