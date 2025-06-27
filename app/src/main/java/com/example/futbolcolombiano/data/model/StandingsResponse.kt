package com.example.futbolcolombiano.data.model

import com.google.gson.annotations.SerializedName

// Clase principal para la respuesta del endpoint /standings
data class StandingsResponse(
    @SerializedName("get")
    val get: String?,
    @SerializedName("parameters")
    val parameters: Map<String, String>?,
    @SerializedName("errors")
    val errors: List<Any>?,
    @SerializedName("results")
    val results: Int?,
    @SerializedName("paging")
    val paging: Paging?, // Reutilizamos Paging
    @SerializedName("response")
    val response: List<LeagueStandings>? // La respuesta es una lista, donde cada elemento es una liga con sus tablas
)

data class LeagueStandings(
    @SerializedName("league")
    val league: ApiLeagueStandingsData?
)

data class ApiLeagueStandingsData(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("logo")
    val logo: String?, // URL del logo de la liga
    @SerializedName("flag")
    val flag: String?, // URL de la bandera del país
    @SerializedName("season")
    val season: Int?, // Año de la temporada
    // La tabla de posiciones es una lista de listas, porque una liga puede tener múltiples grupos/tablas (ej. Apertura/Clausura, Grupo A/B)
    @SerializedName("standings")
    val standings: List<List<ApiStandingDetail>>?
)

data class ApiStandingDetail(
    @SerializedName("rank")
    val rank: Int?,
    @SerializedName("team")
    val team: ApiTeamData?, // Reutilizamos ApiTeamData de FixturesResponse
    @SerializedName("points")
    val points: Int?,
    @SerializedName("goalsDiff")
    val goalsDiff: Int?,
    @SerializedName("group")
    val group: String?, // Nombre del grupo, ej: "Liga BetPlay I 2023"
    @SerializedName("form")
    val form: String?, // Ej: "WWLDW"
    @SerializedName("status")
    val status: String?, // Ej: "same", "up", "down" (relativo a la jornada anterior)
    @SerializedName("description")
    val description: String?, // Ej: "Promotion - Copa Libertadores"
    @SerializedName("all")
    val allMatches: ApiStandingMatches?, // Partidos totales
    @SerializedName("home")
    val homeMatches: ApiStandingMatches?, // Partidos de local
    @SerializedName("away")
    val awayMatches: ApiStandingMatches?, // Partidos de visitante
    @SerializedName("update")
    val update: String? // Timestamp de la última actualización, ej: "2023-11-25T00:00:00+00:00"
)

data class ApiStandingMatches(
    @SerializedName("played")
    val played: Int?,
    @SerializedName("win")
    val win: Int?,
    @SerializedName("draw")
    val draw: Int?,
    @SerializedName("lose")
    val lose: Int?,
    @SerializedName("goals")
    val goals: ApiStandingGoals?
)

data class ApiStandingGoals(
    @SerializedName("for")
    val goalsFor: Int?,
    @SerializedName("against")
    val goalsAgainst: Int?
)
