package com.example.futbolcolombiano.data.model

// Modelo de la App para mostrar estadísticas de un jugador en una temporada/liga
data class PlayerSeasonStats(
    val rank: Int, // El ranking lo podríamos generar al ordenar la lista o si la API lo provee
    val playerId: Int?,
    val playerName: String?,
    val playerPhotoUrl: String?, // URL de la foto del jugador
    val teamId: Int?,
    val teamName: String?,
    val teamLogoUrl: String?, // URL del logo del equipo
    val goals: Int?,
    val assists: Int?,
    val appearances: Int?, // Partidos jugados
    val minutesPlayed: Int?,
    val nationality: String? // Añadido por si es útil
)
