package com.example.futbolcolombiano.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "competitions")
data class CompetitionEntity(
    @PrimaryKey // Asumimos que el 'id' de nuestro modelo Competition (que viene del id de la API como String) es único
    val id: String,
    val name: String,
    val category: String, // Podría ser el nombre del país o una categoría más general
    val gender: String,
    val logoUrl: String?,

    val apiLeagueId: Int, // El ID numérico original de la API, útil para futuras llamadas a la API
    val countryName: String?, // Si queremos guardar el nombre del país asociado
    val currentSeasonYear: Int?, // El año de la temporada que se considera actual para esta liga

    var lastUpdated: Long = System.currentTimeMillis() // Timestamp de la última actualización en el caché
)
