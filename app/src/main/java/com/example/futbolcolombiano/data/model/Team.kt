package com.example.futbolcolombiano.data.model

data class Team(
    val id: String, // Identificador único
    val name: String, // Nombre completo: "Millonarios FC", "Selección Colombia"
    val shortName: String? = null, // Nombre corto: "MIL", "COL"
    val country: String? = null, // Para diferenciar equipos en torneos internacionales
    val logoUrl: String? = null
)
