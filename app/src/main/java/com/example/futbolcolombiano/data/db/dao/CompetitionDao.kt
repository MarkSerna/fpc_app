package com.example.futbolcolombiano.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.futbolcolombiano.data.db.entity.CompetitionEntity

@Dao
interface CompetitionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(competitions: List<CompetitionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(competition: CompetitionEntity)

    @Query("SELECT * FROM competitions ORDER BY name ASC") // Ordenadas por nombre para la UI
    suspend fun getAllCompetitions(): List<CompetitionEntity>

    @Query("SELECT * FROM competitions WHERE id = :id")
    suspend fun getCompetitionById(id: String): CompetitionEntity?

    @Query("SELECT * FROM competitions WHERE apiLeagueId = :apiLeagueId")
    suspend fun getCompetitionByApiLeagueId(apiLeagueId: Int): CompetitionEntity?

    @Query("DELETE FROM competitions")
    suspend fun clearAll()

    // Podríamos añadir un método para obtener la última hora de actualización si es necesario
    @Query("SELECT MAX(lastUpdated) FROM competitions")
    suspend fun getLatestUpdateTimestamp(): Long?
}
