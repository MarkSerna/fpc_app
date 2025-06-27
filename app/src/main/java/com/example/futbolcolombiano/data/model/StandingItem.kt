package com.example.futbolcolombiano.data.model

data class StandingItem(
    val rank: Int,
    val team: Team,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int,
    val points: Int,
    val form: List<Char>? = null // Ej: ['W', 'D', 'L', 'W', 'W']
)
