package com.example.baismcw3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baismcw3.data.ApiClient
import com.example.baismcw3.data.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Screen { LIST, DETAILS }

data class UiState(
    val screen: Screen = Screen.LIST,
    val characters: List<Character> = emptyList(),
    val totalCount: Int = 0,
    val nextPage: Int? = 1,
    val requestedId: Int? = null,
    val character: Character? = null,
    val loading: Boolean = false,
    val error: String? = null,
)

class MainViewModel : ViewModel() {
    private val api = ApiClient.api

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun loadCharacters() {
        _state.update { it.copy(screen = Screen.LIST, characters = emptyList(), nextPage = 1) }
        loadNextPage()
    }

    fun loadNextPage() {
        val page = _state.value.nextPage ?: return
        if (_state.value.loading) return
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            runCatching { api.getCharacters(page) }
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            characters = it.characters + result.results,
                            totalCount = result.info.count,
                            nextPage = if (result.info.next != null) page + 1 else null,
                            loading = false,
                        )
                    }
                }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.describe()) } }
        }
    }

    fun loadCharacter(id: Int) {
        _state.update {
            it.copy(screen = Screen.DETAILS, requestedId = id, character = null, loading = true, error = null)
        }
        viewModelScope.launch {
            runCatching { api.getCharacter(id) }
                .onSuccess { c -> _state.update { it.copy(character = c, loading = false) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.describe()) } }
        }
    }

    fun showList() = _state.update { it.copy(screen = Screen.LIST) }

    private fun Throwable.describe(): String =
        generateSequence(this) { it.cause }
            .joinToString("\n\nCaused by: ") { "${it.javaClass.name}: ${it.message}" }
}
