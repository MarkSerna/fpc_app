package com.example.futbolcolombiano.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolcolombiano.data.db.AppDatabase // Necesario para el constructor por defecto del repo
import com.example.futbolcolombiano.data.model.Competition
import com.example.futbolcolombiano.data.remote.ApiClient
import com.example.futbolcolombiano.data.repository.CompetitionRepository
import com.example.futbolcolombiano.data.util.ApiResult // Importar ApiResult
import kotlinx.coroutines.launch
import android.app.Application // Para el contexto de la BD en el constructor por defecto

// TODO: Implementar inyección de dependencias (Hilt/Koin) para el Repository y Application Context
class MainViewModel(
    // El constructor por defecto ahora necesita el DAO, que a su vez necesita la BD, que necesita Context.
    // Esto se vuelve complejo sin DI. Para simplificar el ejemplo, asumimos que Application es accesible.
    // ¡ESTO NO ES IDEAL PARA PRODUCCIÓN! Se debe usar DI.
    application: Application, // Solo para el constructor por defecto
    private val repository: CompetitionRepository =
        CompetitionRepository(
            ApiClient.instance,
            AppDatabase.getInstance(application).competitionDao(),
            AppDatabase.getInstance(application).standingsDao()
            // Añadir otros DAOs si el repo los necesita
        )
) : ViewModel() {

    private val _competitions = MutableLiveData<List<Competition>>()
    val competitions: LiveData<List<Competition>> get() = _competitions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun loadCompetitions() {
        viewModelScope.launch {
            _isLoading.value = true
            // _error.value = null // Se maneja dentro del when

            when (val result = repository.getCompetitions()) {
                is ApiResult.Success -> {
                    val comps = result.data
                    if (comps.isEmpty()) {
                        _error.value = "No se encontraron competiciones."
                    } else {
                        _error.value = null // Limpiar error si hay éxito y datos
                    }
                    _competitions.value = comps
                }
                is ApiResult.Error -> {
                    _error.value = result.message
                    // Opcional: si el error indica que se están usando datos cacheados,
                    // podríamos querer mostrar los datos cacheados si _competitions.value no está vacío.
                    // if (_competitions.value.isNullOrEmpty()) {
                    //    _competitions.value = emptyList()
                    // }
                    // Por ahora, si hay error, no actualizamos la lista de competiciones (se mantiene la anterior o vacía).
                }
            }
            _isLoading.value = false
        }
    }
}
