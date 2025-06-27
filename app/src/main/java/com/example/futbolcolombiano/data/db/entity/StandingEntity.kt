package com.example.futbolcolombiano.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Usaremos una clave primaria compuesta para identificar una fila única en la tabla de posiciones:
// el ID de la liga/competición y el ID del equipo.
// También podríamos añadir el nombre del grupo si la liga tiene múltiples tablas (ej. "Grupo A", "Apertura").
// Por ahora, simplificaremos asumiendo una tabla principal por liga/temporada.

@Entity(
    tableName = "standings",
    primaryKeys = ["competitionApiId", "teamId"], // Clave primaria compuesta
    foreignKeys = [
        ForeignKey(
            entity = CompetitionEntity::class,
            parentColumns = ["apiLeagueId"], // Debe coincidir con el PK de CompetitionEntity si es apiLeagueId
            childColumns = ["competitionApiId"],
            onDelete = ForeignKey.CASCADE
        )
        // Podríamos tener una TeamEntity y enlazar teamId a ella también si la normalizamos.
        // Por ahora, teamId es solo un Int.
    ],
    indices = [Index(value = ["competitionApiId"])]
)
data class StandingEntity(
    val competitionApiId: Int, // Parte de la PK y FK
    val teamId: Int,           // Parte de la PK, ID del equipo de la API

    val rank: Int,
    val teamName: String,
    val teamLogoUrl: String?,

    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int,
    val points: Int,

    val form: String?, // Ej: "WWLDW", almacenado como String
    val groupName: String?, // Nombre del grupo/tabla, ej: "Liga BetPlay I 2023"
    val description: String?, // Ej: "Promotion - Copa Libertadores"

    var lastUpdated: Long = System.currentTimeMillis()
)
