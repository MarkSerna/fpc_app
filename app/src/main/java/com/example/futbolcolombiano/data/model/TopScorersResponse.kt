package com.example.futbolcolombiano.data.model

import com.google.gson.annotations.SerializedName

// Clase principal para la respuesta del endpoint /players/topscorers (o similar)
data class TopScorersResponse(
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
    val response: List<ApiPlayerScorerEntry>?
)

data class ApiPlayerScorerEntry(
    @SerializedName("player")
    val player: ApiPlayerDetails?,
    // Las estadísticas suelen venir en una lista, ya que un jugador puede haber jugado
    // para múltiples equipos en una temporada o la API puede devolver diferentes
    // tipos de estadísticas agregadas. Tomaremos la primera por simplicidad.
    @SerializedName("statistics")
    val statistics: List<ApiPlayerStatisticsData>?
)

data class ApiPlayerDetails(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?, // A veces es el nombre completo
    @SerializedName("firstname")
    val firstname: String?,
    @SerializedName("lastname")
    val lastname: String?,
    @SerializedName("age")
    val age: Int?,
    @SerializedName("nationality")
    val nationality: String?,
    @SerializedName("photo")
    val photo: String? // URL de la foto del jugador
)

data class ApiPlayerStatisticsData(
    @SerializedName("team")
    val team: ApiTeamData?, // Reutilizamos ApiTeamData (id, name, logo) de FixturesResponse
    @SerializedName("league")
    val league: ApiLeague?, // Reutilizamos ApiLeague (id, name, logo, type) de LeagueResponse
    @SerializedName("games")
    val games: ApiPlayerGamesStats?,
    @SerializedName("goals")
    val goals: ApiPlayerGoalsStats?,
    @SerializedName("cards")
    val cards: ApiPlayerCardsStats?
    // Podría haber más como "passes", "dribbles", etc.
)

data class ApiPlayerGamesStats(
    @SerializedName("appearences") // API-Football a veces tiene "appearences" en lugar de "appearances"
    val appearances: Int?,
    @SerializedName("minutes")
    val minutes: Int?,
    @SerializedName("rating")
    val rating: String? // A veces es un string "7.8"
)

data class ApiPlayerGoalsStats(
    @SerializedName("total")
    val total: Int?,
    @SerializedName("conceded")
    val conceded: Int?, // Para porteros
    @SerializedName("assists")
    val assists: Int?,
    @SerializedName("saves")
    val saves: Int? // Para porteros
)

data class ApiPlayerCardsStats(
    @SerializedName("yellow")
    val yellow: Int?,
    @SerializedName("red")
    val red: Int?
)
