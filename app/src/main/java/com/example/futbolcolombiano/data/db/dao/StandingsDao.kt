package com.example.futbolcolombiano.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.futbolcolombiano.data.db.entity.StandingEntity

@Dao
interface StandingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(standings: List<StandingEntity>)

    @Query("SELECT * FROM standings WHERE competitionApiId = :competitionApiId ORDER BY rank ASC")
    suspend fun getStandingsByCompetition(competitionApiId: Int): List<StandingEntity>

    // Para obtener la última actualización específica de una tabla de posiciones de una competición
    @Query("SELECT MAX(lastUpdated) FROM standings WHERE competitionApiId = :competitionApiId")
    suspend fun getLatestUpdateTimestampForCompetition(competitionApiId: Int): Long?

    @Query("DELETE FROM standings WHERE competitionApiId = :competitionApiId")
    suspend fun clearStandingsForCompetition(competitionApiId: Int)

    @Query("DELETE FROM standings")
    suspend fun clearAll() // En caso de necesitar limpiar toda la tabla
}
