package com.example.futbolcolombiano.data.model

import java.util.Date // Considerar usar java.time para API 26+

data class Match(
    val id: String, // Identificador único del partido
    val competitionId: String,
    val localTeam: Team,
    val visitorTeam: Team,
    val localScore: Int?,
    val visitorScore: Int?,
    val dateTime: Date, // Fecha y hora del partido
    val status: MatchStatus, // Ej: SCHEDULED, LIVE, FINISHED, POSTPONED
    val round: String? = null, // Ej: "Fase de Grupos - Jornada 1", "Octavos de Final"
    val stadium: String? = null,
    val liveMinute: String? = null // Ej: "45+2'", "Entretiempo"
)

enum class MatchStatus {
    SCHEDULED,
    LIVE,
    FINISHED,
    POSTPONED,
    CANCELED
}
