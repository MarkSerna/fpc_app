package com.example.futbolcolombiano.data.model

import com.google.gson.annotations.SerializedName

// Clase principal para la respuesta del endpoint /fixtures
data class FixturesResponse(
    @SerializedName("get")
    val get: String?,
    @SerializedName("parameters")
    val parameters: Map<String, String>?,
    @SerializedName("errors")
    val errors: List<Any>?,
    @SerializedName("results")
    val results: Int?,
    @SerializedName("paging")
    val paging: Paging?, // Reutilizamos la clase Paging de LeagueResponse
    @SerializedName("response")
    val response: List<FixtureData>?
)

data class FixtureData(
    @SerializedName("fixture")
    val fixture: ApiFixtureDetails?,
    @SerializedName("league")
    val league: ApiLeague?, // Reutilizamos ApiLeague
    @SerializedName("teams")
    val teams: ApiFixtureTeams?,
    @SerializedName("goals")
    val goals: ApiFixtureGoals?,
    @SerializedName("score")
    val score: ApiFixtureScore?
    // Podría haber más información como "events", "lineups", "statistics"
)

data class ApiFixtureDetails(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("referee")
    val referee: String?,
    @SerializedName("timezone")
    val timezone: String?, // Ej: "UTC"
    @SerializedName("date")
    val date: String?, // Fecha y hora en formato ISO 8601, ej: "2023-11-25T20:00:00+00:00"
    @SerializedName("timestamp")
    val timestamp: Long?,
    @SerializedName("periods")
    val periods: ApiFixturePeriods?,
    @SerializedName("venue")
    val venue: ApiVenue?,
    @SerializedName("status")
    val status: ApiFixtureStatus?
)

data class ApiFixturePeriods(
    @SerializedName("first")
    val first: Long?, // Timestamp de inicio del primer tiempo
    @SerializedName("second")
    val second: Long? // Timestamp de inicio del segundo tiempo
)

data class ApiVenue(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("city")
    val city: String?
)

data class ApiFixtureStatus(
    @SerializedName("long")
    val long: String?, // Ej: "Match Finished", "Not Started", "Halftime"
    @SerializedName("short")
    val short: String?, // Ej: "FT", "NS", "HT", "LIVE" (API-Football usa varios códigos aquí)
    @SerializedName("elapsed")
    val elapsed: Int? // Minutos jugados si el partido está en vivo
)

data class ApiFixtureTeams(
    @SerializedName("home")
    val home: ApiTeamData?,
    @SerializedName("away")
    val away: ApiTeamData?
)

data class ApiTeamData( // Similar a nuestro modelo Team, pero adaptado a la API
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("logo")
    val logo: String?, // URL del logo
    @SerializedName("winner")
    val winner: Boolean? // True si este equipo ganó, null si no o si el partido no ha terminado
)

data class ApiFixtureGoals(
    @SerializedName("home")
    val home: Int?,
    @SerializedName("away")
    val away: Int?
)

data class ApiFixtureScore(
    @SerializedName("halftime")
    val halftime: ApiScoreDetail?,
    @SerializedName("fulltime")
    val fulltime: ApiScoreDetail?,
    @SerializedName("extratime")
    val extratime: ApiScoreDetail?,
    @SerializedName("penalty")
    val penalty: ApiScoreDetail?
)

data class ApiScoreDetail(
    @SerializedName("home")
    val home: Int?,
    @SerializedName("away")
    val away: Int?
)
