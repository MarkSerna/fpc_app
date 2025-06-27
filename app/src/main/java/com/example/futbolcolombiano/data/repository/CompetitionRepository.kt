package com.example.futbolcolombiano.data.repository

import android.content.Context
import com.example.futbolcolombiano.data.db.AppDatabase
import com.example.futbolcolombiano.data.db.dao.CompetitionDao
import com.example.futbolcolombiano.data.db.dao.StandingsDao
import com.example.futbolcolombiano.data.db.entity.CompetitionEntity
import com.example.futbolcolombiano.data.db.entity.StandingEntity
import com.example.futbolcolombiano.data.model.ApiStandingDetail
import com.example.futbolcolombiano.data.model.Competition
import com.example.futbolcolombiano.data.model.LeagueData
import com.example.futbolcolombiano.data.model.Match
import com.example.futbolcolombiano.data.model.PlayerSeasonStats
import com.example.futbolcolombiano.data.model.StandingItem
import com.example.futbolcolombiano.data.model.Team
import com.example.futbolcolombiano.data.model.MatchStatus
import com.example.futbolcolombiano.data.remote.ApiClient
import com.example.futbolcolombiano.data.remote.FootballApiService
import com.example.futbolcolombiano.data.util.ApiResult // Importar ApiResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class CompetitionRepository(
    private val apiService: FootballApiService = ApiClient.instance,
    private val competitionDao: CompetitionDao,
    private val standingsDao: StandingsDao
    // private val matchDao: MatchDao,
    // private val playerStatsDao: PlayerStatsDao
) {

    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000

    suspend fun getCompetitions(): ApiResult<List<Competition>> {
        val cachedCompetitions = competitionDao.getAllCompetitions()
        val lastUpdate = competitionDao.getLatestUpdateTimestamp() ?: 0L
        val now = System.currentTimeMillis()

        if (cachedCompetitions.isNotEmpty() && (now - lastUpdate < CACHE_EXPIRY_MS)) {
            println("CACHE HIT: Competitions")
            return ApiResult.Success(cachedCompetitions.map { mapCompetitionEntityToCompetition(it) })
        }

        println("CACHE MISS/EXPIRED: Competitions - Fetching from API")
        return try {
            val response = apiService.getLeagues()
            if (response.isSuccessful) {
                val apiLeaguesData = response.body()?.response ?: emptyList()
                val competitionEntities = apiLeaguesData.mapNotNull { mapLeagueDataToCompetitionEntity(it) }

                competitionDao.clearAll()
                competitionDao.insertAll(competitionEntities)

                ApiResult.Success(competitionEntities.map { mapCompetitionEntityToCompetition(it) })
            } else {
                val errorMsg = "Error API getLeagues: ${response.code()} - ${response.message()}"
                System.err.println(errorMsg)
                // Si la API falla pero tenemos cache (aunque sea viejo), podríamos devolverlo como un tipo de éxito diferente o error con datos.
                // Aquí devolvemos error pero el ViewModel podría decidir usar el caché si existe.
                if (cachedCompetitions.isNotEmpty()) {
                     // Podríamos tener un ApiResult.SuccessWithStaleData(cachedCompetitions.map { mapCompetitionEntityToCompetition(it) }, errorMsg)
                     // O simplemente devolver el error y dejar que el ViewModel lo maneje.
                    ApiResult.Error(errorMsg + " (mostrando datos cacheados si existen)", response.code())
                } else {
                    ApiResult.Error(errorMsg, response.code())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val errorMsg = "Excepción al obtener competiciones: ${e.localizedMessage}"
             if (cachedCompetitions.isNotEmpty()) {
                ApiResult.Error(errorMsg + " (mostrando datos cacheados si existen)")
            } else {
                ApiResult.Error(errorMsg)
            }
        }
    }

    suspend fun getMatches(competitionId: Int, seasonYear: Int): ApiResult<List<Match>> {
        // TODO: Implementar lógica de cacheo para partidos
        return try {
            val response = apiService.getFixtures(leagueId = competitionId, season = seasonYear)
            if (response.isSuccessful) {
                val matches = response.body()?.response?.mapNotNull { fixtureData ->
                    mapApiFixtureToMatch(fixtureData, competitionId.toString())
                } ?: emptyList()
                ApiResult.Success(matches)
            } else {
                ApiResult.Error("Error API getFixtures: ${response.code()} - ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error("Excepción al obtener partidos: ${e.localizedMessage}")
        }
    }

    suspend fun getStandings(competitionApiId: Int, seasonYear: Int): ApiResult<List<StandingItem>> {
        val cachedStandings = standingsDao.getStandingsByCompetition(competitionApiId)
        val lastUpdate = standingsDao.getLatestUpdateTimestampForCompetition(competitionApiId) ?: 0L
        val now = System.currentTimeMillis()

        if (cachedStandings.isNotEmpty() && (now - lastUpdate < CACHE_EXPIRY_MS)) {
            println("CACHE HIT: Standings for competition $competitionApiId")
            return ApiResult.Success(cachedStandings.map { mapStandingEntityToStandingItem(it) })
        }

        println("CACHE MISS/EXPIRED: Standings for $competitionApiId - Fetching from API")
        return try {
            val response = apiService.getStandings(leagueId = competitionApiId, season = seasonYear)
            if (response.isSuccessful) {
                val firstLeagueStandingsFromApi = response.body()?.response?.firstOrNull()?.league?.standings?.firstOrNull()
                val standingEntities = firstLeagueStandingsFromApi?.mapNotNull { apiStandingDetail ->
                    mapApiStandingDetailToStandingEntity(apiStandingDetail, competitionApiId)
                } ?: emptyList()

                if (standingEntities.isNotEmpty()) {
                    standingsDao.clearStandingsForCompetition(competitionApiId)
                    standingsDao.insertAll(standingEntities)
                }
                ApiResult.Success(standingEntities.map { mapStandingEntityToStandingItem(it) })
            } else {
                val errorMsg = "Error API getStandings: ${response.code()} - ${response.message()}"
                System.err.println(errorMsg)
                if (cachedStandings.isNotEmpty()) {
                    ApiResult.Error(errorMsg + " (mostrando datos cacheados si existen)", response.code())
                } else {
                    ApiResult.Error(errorMsg, response.code())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val errorMsg = "Excepción al obtener tabla de posiciones: ${e.localizedMessage}"
            if (cachedStandings.isNotEmpty()) {
                 ApiResult.Error(errorMsg + " (mostrando datos cacheados si existen)")
            } else {
                ApiResult.Error(errorMsg)
            }
        }
    }

    suspend fun getTopScorers(leagueId: Int, season: Int): ApiResult<List<PlayerSeasonStats>> {
        // TODO: Implementar lógica de cacheo para goleadores
        return try {
            val response = apiService.getTopScorers(leagueId = leagueId, season = season)
            if (response.isSuccessful) {
                val scorers = response.body()?.response?.mapNotNull { apiPlayerScorerEntry ->
                    mapApiPlayerScorerToPlayerSeasonStats(apiPlayerScorerEntry)
                }?.sortedByDescending { it.goals }
                ?.mapIndexed { index, playerStats -> playerStats.copy(rank = index + 1) }
                ?: emptyList()
                ApiResult.Success(scorers)
            } else {
                ApiResult.Error("Error API getTopScorers: ${response.code()} - ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error("Excepción al obtener goleadores: ${e.localizedMessage}")
        }
    }

    // --- Funciones de Mapeo ---
    // (Las funciones de mapeo permanecen igual, solo se ajusta el tipo de retorno de los métodos públicos)

    private fun mapLeagueDataToCompetitionEntity(leagueData: LeagueData): CompetitionEntity? {
        val apiLeague = leagueData.league ?: return null
        val apiCountry = leagueData.country
        val currentApiSeason = leagueData.seasons?.find { it.current == true }

        return CompetitionEntity(
            id = apiLeague.id?.toString() ?: return null,
            name = apiLeague.name ?: "N/A",
            category = apiCountry?.name ?: "Internacional",
            gender = determineGenderFromLeagueName(apiLeague.name),
            logoUrl = apiLeague.logo,
            apiLeagueId = apiLeague.id,
            countryName = apiCountry?.name,
            currentSeasonYear = currentApiSeason?.year,
            lastUpdated = System.currentTimeMillis()
        )
    }

    private fun mapCompetitionEntityToCompetition(entity: CompetitionEntity): Competition {
        return Competition(
            id = entity.id,
            name = entity.name,
            category = entity.category,
            gender = entity.gender,
            logoUrl = entity.logoUrl
        )
    }

    private fun mapApiStandingDetailToStandingEntity(detail: ApiStandingDetail, competitionApiId: Int): StandingEntity? {
        val teamApi = detail.team ?: return null
        val teamId = teamApi.id ?: return null
        val allMatches = detail.allMatches ?: return null

        return StandingEntity(
            competitionApiId = competitionApiId,
            teamId = teamId,
            rank = detail.rank ?: 0,
            teamName = teamApi.name ?: "N/A",
            teamLogoUrl = teamApi.logo,
            played = allMatches.played ?: 0,
            wins = allMatches.win ?: 0,
            draws = allMatches.draw ?: 0,
            losses = allMatches.lose ?: 0,
            goalsFor = allMatches.goals?.goalsFor ?: 0,
            goalsAgainst = allMatches.goals?.goalsAgainst ?: 0,
            goalDifference = detail.goalsDiff ?: 0,
            points = detail.points ?: 0,
            form = detail.form,
            groupName = detail.group,
            description = detail.description,
            lastUpdated = System.currentTimeMillis()
        )
    }

    private fun mapStandingEntityToStandingItem(entity: StandingEntity): StandingItem {
        return StandingItem(
            rank = entity.rank,
            team = Team(
                id = entity.teamId.toString(),
                name = entity.teamName,
                logoUrl = entity.teamLogoUrl
            ),
            played = entity.played,
            wins = entity.wins,
            draws = entity.draws,
            losses = entity.losses,
            goalsFor = entity.goalsFor,
            goalsAgainst = entity.goalsAgainst,
            goalDifference = entity.goalDifference,
            points = entity.points,
            form = entity.form?.toList()
        )
    }

    private fun mapApiFixtureToMatch(fixtureData: com.example.futbolcolombiano.data.model.FixtureData, compId: String): Match? {
        val fixtureDetails = fixtureData.fixture ?: return null
        val teams = fixtureData.teams ?: return null
        val homeTeamApi = teams.home ?: return null
        val awayTeamApi = teams.away ?: return null
        val goals = fixtureData.goals

        val homeTeam = Team(
            id = homeTeamApi.id?.toString() ?: return null,
            name = homeTeamApi.name ?: "Local",
            logoUrl = homeTeamApi.logo
        )
        val awayTeam = Team(
            id = awayTeamApi.id?.toString() ?: return null,
            name = awayTeamApi.name ?: "Visitante",
            logoUrl = awayTeamApi.logo
        )

        val dateTime = fixtureDetails.date?.let { parseApiDate(it) } ?: Date()

        return Match(
            id = fixtureDetails.id?.toString() ?: return null,
            competitionId = compId,
            localTeam = homeTeam,
            visitorTeam = awayTeam,
            localScore = goals?.home,
            visitorScore = goals?.away,
            dateTime = dateTime,
            status = mapApiFixtureStatus(fixtureDetails.status?.short, fixtureDetails.status?.elapsed),
            round = fixtureData.league?.name,
            stadium = fixtureDetails.venue?.name,
            liveMinute = fixtureDetails.status?.elapsed?.toString() ?: if (fixtureDetails.status?.short == "HT") "Entretiempo" else null
        )
    }

    private fun mapApiStandingDetailToStandingItem(detail: com.example.futbolcolombiano.data.model.ApiStandingDetail): StandingItem? {
        val teamApi = detail.team ?: return null
        val team = Team(
            id = teamApi.id?.toString() ?: return null,
            name = teamApi.name ?: "Equipo Desconocido",
            logoUrl = teamApi.logo
        )
        val allMatches = detail.allMatches ?: return null

        return StandingItem(
            rank = detail.rank ?: 0,
            team = team,
            played = allMatches.played ?: 0,
            wins = allMatches.win ?: 0,
            draws = allMatches.draw ?: 0,
            losses = allMatches.lose ?: 0,
            goalsFor = allMatches.goals?.goalsFor ?: 0,
            goalsAgainst = allMatches.goals?.goalsAgainst ?: 0,
            goalDifference = detail.goalsDiff ?: 0,
            points = detail.points ?: 0,
            form = detail.form?.toList()
        )
    }

    private fun mapApiPlayerScorerToPlayerSeasonStats(entry: com.example.futbolcolombiano.data.model.ApiPlayerScorerEntry): PlayerSeasonStats? {
        val playerDetails = entry.player ?: return null
        val statsData = entry.statistics?.firstOrNull() ?: return null
        val teamData = statsData.team
        val goalsData = statsData.goals
        val gamesData = statsData.games

        return PlayerSeasonStats(
            rank = 0,
            playerId = playerDetails.id,
            playerName = playerDetails.name ?: "${playerDetails.firstname ?: ""} ${playerDetails.lastname ?: ""}".trim(),
            playerPhotoUrl = playerDetails.photo,
            teamId = teamData?.id,
            teamName = teamData?.name,
            teamLogoUrl = teamData?.logo,
            goals = goalsData?.total,
            assists = goalsData?.assists,
            appearances = gamesData?.appearances,
            minutesPlayed = gamesData?.minutes,
            nationality = playerDetails.nationality
        )
    }

    private fun parseApiDate(dateString: String): Date {
        return try {
            apiDateFormat.parse(dateString) ?: Date()
        } catch (e: Exception) {
            e.printStackTrace()
            Date()
        }
    }

    private fun mapApiFixtureStatus(shortStatus: String?, elapsed: Int?): MatchStatus {
        return when (shortStatus) {
            "TBD", "NS" -> MatchStatus.SCHEDULED
            "LIVE", "1H", "HT", "2H", "ET", "BT", "P", "INT" -> MatchStatus.LIVE
            "FT", "AET", "PEN" -> MatchStatus.FINISHED
            "PST" -> MatchStatus.POSTPONED
            "CANC" -> MatchStatus.CANCELED
            "ABD" -> MatchStatus.CANCELED
            "AWD" -> MatchStatus.FINISHED
            "WO" -> MatchStatus.FINISHED
            else -> if (elapsed != null && elapsed > 0) MatchStatus.LIVE else MatchStatus.SCHEDULED
        }
    }

    private fun determineGenderFromLeagueName(leagueName: String?): String {
        leagueName?.let {
            if (it.contains("Femenina", ignoreCase = true) || it.contains("Women", ignoreCase = true)) {
                return "Femenino"
            }
        }
        return "Masculino"
    }
}
