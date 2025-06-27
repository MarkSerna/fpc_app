package com.example.futbolcolombiano.ui.leaguedetails

import android.app.Application // Para el constructor por defecto del repo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolcolombiano.data.db.AppDatabase // Necesario para el constructor por defecto del repo
import com.example.futbolcolombiano.data.model.Match
import com.example.futbolcolombiano.data.model.PlayerSeasonStats
import com.example.futbolcolombiano.data.model.StandingItem
import com.example.futbolcolombiano.data.remote.ApiClient
import com.example.futbolcolombiano.data.repository.CompetitionRepository
import com.example.futbolcolombiano.data.util.ApiResult // Importar ApiResult
import kotlinx.coroutines.launch
import java.util.Calendar

// TODO: Implementar inyección de dependencias (Hilt/Koin) para el Repository y Application Context
class LeagueDetailsViewModel(
    application: Application, // Solo para el constructor por defecto
    private val repository: CompetitionRepository =
        CompetitionRepository(
            ApiClient.instance,
            AppDatabase.getInstance(application).competitionDao(),
            AppDatabase.getInstance(application).standingsDao()
            // Añadir otros DAOs si el repo los necesita
        )
) : ViewModel() {

    // Matches
    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>> get() = _matches
    private val _isLoadingMatches = MutableLiveData<Boolean>()
    val isLoadingMatches: LiveData<Boolean> get() = _isLoadingMatches
    private val _errorMatches = MutableLiveData<String?>()
    val errorMatches: LiveData<String?> get() = _errorMatches

    // Standings
    private val _standings = MutableLiveData<List<StandingItem>>()
    val standings: LiveData<List<StandingItem>> get() = _standings
    private val _isLoadingStandings = MutableLiveData<Boolean>()
    val isLoadingStandings: LiveData<Boolean> get() = _isLoadingStandings
    private val _errorStandings = MutableLiveData<String?>()
    val errorStandings: LiveData<String?> get() = _errorStandings

    // Top Scorers
    private val _topScorers = MutableLiveData<List<PlayerSeasonStats>>()
    val topScorers: LiveData<List<PlayerSeasonStats>> get() = _topScorers
    private val _isLoadingTopScorers = MutableLiveData<Boolean>()
    val isLoadingTopScorers: LiveData<Boolean> get() = _isLoadingTopScorers
    private val _errorTopScorers = MutableLiveData<String?>()
    val errorTopScorers: LiveData<String?> get() = _errorTopScorers

    private val currentSeasonYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    fun loadMatches(competitionIdApi: Int, seasonYear: Int = currentSeasonYear) {
        viewModelScope.launch {
            _isLoadingMatches.value = true
            // _errorMatches.value = null // Se maneja en el when

            when(val result = repository.getMatches(competitionIdApi, seasonYear)) {
                is ApiResult.Success -> {
                    val matchList = result.data
                    if (matchList.isEmpty()) {
                         _errorMatches.value = "No hay partidos disponibles."
                    } else {
                        _errorMatches.value = null
                    }
                    _matches.value = matchList
                }
                is ApiResult.Error -> {
                    _errorMatches.value = result.message
                }
            }
            _isLoadingMatches.value = false
        }
    }

    fun loadStandings(competitionIdApi: Int, seasonYear: Int = currentSeasonYear) {
        viewModelScope.launch {
            _isLoadingStandings.value = true
            // _errorStandings.value = null // Se maneja en el when

            when(val result = repository.getStandings(competitionIdApi, seasonYear)) {
                is ApiResult.Success -> {
                    val standingsList = result.data
                    if (standingsList.isEmpty()) {
                          _errorStandings.value = "Tabla de posiciones no disponible."
                    } else {
                        _errorStandings.value = null
                    }
                    _standings.value = standingsList
                }
                is ApiResult.Error -> {
                     _errorStandings.value = result.message
                }
            }
            _isLoadingStandings.value = false
        }
    }

    fun loadTopScorers(competitionIdApi: Int, seasonYear: Int = currentSeasonYear) {
        viewModelScope.launch {
            _isLoadingTopScorers.value = true
            // _errorTopScorers.value = null // Se maneja en el when

            when(val result = repository.getTopScorers(competitionIdApi, seasonYear)) {
                is ApiResult.Success -> {
                    val scorersList = result.data
                    if (scorersList.isEmpty()) {
                        _errorTopScorers.value = "Goleadores no disponibles."
                    } else {
                        _errorTopScorers.value = null
                    }
                    _topScorers.value = scorersList
                }
                is ApiResult.Error -> {
                    _errorTopScorers.value = result.message
                }
            }
            _isLoadingTopScorers.value = false
        }
    }
}
