package com.ucaldas.taller8

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiState {
    data object Loading : UiState
    data class Success(val items: List<Personaje>) : UiState
    data class Error(val message: String) : UiState
}

sealed interface DetalleUiState {
    data object Loading : DetalleUiState
    data class Success(val personaje: Personaje) : DetalleUiState
    data class Error(val message: String) : DetalleUiState
}

class PersonajesViewModel(
    private val repository: PersonajesRepository = PersonajesRepository(RetrofitInstance.api)
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _detalleUiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Loading)
    val detalleUiState: StateFlow<DetalleUiState> = _detalleUiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = repository.obtenerPersonajes().fold(
                onSuccess = { UiState.Success(it.results) },
                onFailure = { UiState.Error("No se pudo cargar. Reintenta.") }
            )
        }
    }

    /**
     * Ruta temporal de diagnóstico: no captura la excepción para que Logcat
     * muestre el error real producido por Retrofit al perder la conexión.
     * Eliminar o reemplazar por cargar() después de terminar la prueba.
     */
    fun cargarSinManejoDeErrores() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val response = RetrofitInstance.api.obtenerPersonajes()
            _uiState.value = UiState.Success(response.results)
        }
    }

    fun cargarDetalle(id: Int) {
        viewModelScope.launch {
            _detalleUiState.value = DetalleUiState.Loading
            _detalleUiState.value = repository.obtenerPersonajePorId(id).fold(
                onSuccess = { DetalleUiState.Success(it) },
                onFailure = { DetalleUiState.Error("Error al cargar detalle.") }
            )
        }
    }
}
