package com.baism.cw3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baism.cw3.BuildConfig
import com.baism.cw3.data.ApiClient
import com.baism.cw3.data.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val characters: List<Character> = emptyList(),
    val selected: Character? = null,
    val characterIdInput: String = "1",
    val loading: Boolean = false,
    val error: String? = null,
    val statusLine: String = buildStatusLine()
)

private fun buildStatusLine(): String {
    val pinning = if (BuildConfig.ENABLE_CERT_PINNING) "ON" else "OFF"
    val userCa = if (BuildConfig.TRUST_USER_CA) "ON" else "OFF"
    return "Pinning: $pinning | Trust User CA (flag): $userCa"
}

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun onIdChanged(value: String) {
        _state.update { it.copy(characterIdInput = value.filter { ch -> ch.isDigit() }.take(5)) }
    }

    fun loadCharacters() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { ApiClient.api.listCharacters(page = 1) }
                .onSuccess { response ->
                    _state.update {
                        it.copy(
                            loading = false,
                            characters = response.results,
                            error = null,
                            statusLine = buildStatusLine() + " | listed ${response.results.size}"
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            loading = false,
                            error = e.message ?: e.toString(),
                            statusLine = buildStatusLine() + " | list FAILED"
                        )
                    }
                }
        }
    }

    fun loadCharacterById() {
        val id = _state.value.characterIdInput.toIntOrNull()
        if (id == null) {
            _state.update { it.copy(error = "Podaj poprawne ID postaci") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { ApiClient.api.getCharacter(id) }
                .onSuccess { character ->
                    _state.update {
                        it.copy(
                            loading = false,
                            selected = character,
                            error = null,
                            statusLine = buildStatusLine() + " | character #$id OK"
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            loading = false,
                            error = e.message ?: e.toString(),
                            statusLine = buildStatusLine() + " | character #$id FAILED"
                        )
                    }
                }
        }
    }
}
