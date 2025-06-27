package com.example.futbolcolombiano.data.model

import com.google.gson.annotations.SerializedName

// Clase principal para la respuesta del endpoint /leagues
data class LeagueResponse(
    @SerializedName("get")
    val get: String?, // Nombre del endpoint, ej: "leagues"
    @SerializedName("parameters")
    val parameters: Map<String, String>?, // Parámetros usados en la petición
    @SerializedName("errors")
    val errors: List<Any>?, // Lista de errores, puede ser String o un objeto de error más complejo
    @SerializedName("results")
    val results: Int?, // Número de resultados
    @SerializedName("paging")
    val paging: Paging?,
    @SerializedName("response")
    val response: List<LeagueData>? // La lista de ligas
)

data class LeagueData(
    @SerializedName("league")
    val league: ApiLeague?,
    @SerializedName("country")
    val country: ApiCountry?,
    @SerializedName("seasons")
    val seasons: List<ApiSeason>?
)

data class ApiLeague(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: String?, // Ej: "League", "Cup"
    @SerializedName("logo")
    val logo: String? // URL del logo de la liga
)

data class ApiCountry(
    @SerializedName("name")
    val name: String?,
    @SerializedName("code")
    val code: String?, // Ej: "CO", "GB"
    @SerializedName("flag")
    val flag: String? // URL de la bandera del país
)

data class ApiSeason(
    @SerializedName("year")
    val year: Int?,
    @SerializedName("start")
    val start: String?, // Fecha YYYY-MM-DD
    @SerializedName("end")
    val end: String?, // Fecha YYYY-MM-DD
    @SerializedName("current")
    val current: Boolean?,
    // Podría haber más campos como "coverage" aquí
)

data class Paging(
    @SerializedName("current")
    val current: Int?,
    @SerializedName("total")
    val total: Int?
)
