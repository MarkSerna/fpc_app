package com.example.futbolcolombiano.data.remote

import com.example.futbolcolombiano.data.model.LeagueResponse
import com.example.futbolcolombiano.data.model.FixturesResponse
import com.example.futbolcolombiano.data.model.StandingsResponse
import com.example.futbolcolombiano.data.model.TopScorersResponse // Nueva importación
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApiService {

    @GET("leagues")
    suspend fun getLeagues(
        // @Query("country") country: String? = null,
        // @Query("current") current: String? = "true"
        // @Query("id") id: Int? = null
    ): Response<LeagueResponse>

    @GET("fixtures")
    suspend fun getFixtures(
        @Query("league") leagueId: Int,
        @Query("season") season: Int,
        // @Query("date") date: String? = null,
        // @Query("status") status: String? = null,
        // @Query("team") teamId: Int? = null
    ): Response<FixturesResponse>

    @GET("standings")
    suspend fun getStandings(
        @Query("league") leagueId: Int,
        @Query("season") season: Int
    ): Response<StandingsResponse>

    // Nuevo endpoint para goleadores
    @GET("players/topscorers")
    suspend fun getTopScorers(
        @Query("league") leagueId: Int,
        @Query("season") season: Int
    ): Response<TopScorersResponse>

    // Se podrían añadir más endpoints aquí:
    // - /teams?league={id}&season={year}
    // - /players/squads?team={team_id}
    // - /fixtures?id={fixture_id} (para detalles de un partido específico)
    // - etc.
}
