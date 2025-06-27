package com.example.futbolcolombiano.data.model

data class Competition(
    val id: String, // Identificador único para scraping o API interna
    val name: String, // Nombre legible: "Liga BetPlay Dimayor", "Copa Libertadores"
    val category: String, // "Clubes Colombia", "Clubes CONMEBOL", "Selecciones FIFA", "Selecciones CONMEBOL"
    val gender: String, // "Masculino", "Femenino"
    val logoUrl: String? = null
)
