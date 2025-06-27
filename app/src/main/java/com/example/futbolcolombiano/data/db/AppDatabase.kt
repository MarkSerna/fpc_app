package com.example.futbolcolombiano.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.futbolcolombiano.data.db.dao.CompetitionDao
import com.example.futbolcolombiano.data.db.dao.StandingsDao // Nueva importación
// import com.example.futbolcolombiano.data.db.dao.MatchDao
// import com.example.futbolcolombiano.data.db.dao.PlayerStatsDao
import com.example.futbolcolombiano.data.db.entity.CompetitionEntity
import com.example.futbolcolombiano.data.db.entity.StandingEntity // Nueva importación
// import com.example.futbolcolombiano.data.db.entity.MatchEntity
// import com.example.futbolcolombiano.data.db.entity.PlayerStatsEntity

@Database(
    entities = [
        CompetitionEntity::class,
        StandingEntity::class // Añadida StandingEntity
        // , MatchEntity::class
        // , PlayerStatsEntity::class
    ],
    version = 1, // Mantener en 1 por ahora, incrementar si se cambia el esquema DE VERDAD
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun competitionDao(): CompetitionDao
    abstract fun standingsDao(): StandingsDao // Nuevo DAO
    // abstract fun matchDao(): MatchDao
    // abstract fun playerStatsDao(): PlayerStatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "futbol_colombiano_app.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                // .fallbackToDestructiveMigration() // Usar con cuidado en producción, solo para desarrollo
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
