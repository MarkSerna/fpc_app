package com.example.futbolcolombiano.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
// import com.example.futbolcolombiano.BuildConfig // Si la API key estuviera en BuildConfig

object ApiClient {

    // TODO: Mover la API Key a un lugar seguro (gradle.properties, BuildConfig, etc.)
    // ¡NO DEJAR EN CÓDIGO VERSIONADO EN PRODUCCIÓN!
    private const val API_KEY = "224f670d881f9349bb233b4e02d22fe1"
    private const val BASE_URL = "https://v3.football.api-sports.io/"
    private const val API_HOST = "v3.football.api-sports.io"

    // Crear el logger
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Loguea el cuerpo de la petición y respuesta
    }

    // Crear el cliente OkHttp
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("x-rapidapi-host", API_HOST)
                .header("x-rapidapi-key", API_KEY) // ¡USAR CON PRECAUCIÓN!
                .method(original.method, original.body)

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor) // Añadir el logger de OkHttp
        .build()

    // Crear la instancia de Retrofit
    val instance: FootballApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Usar el cliente OkHttp configurado
            .addConverterFactory(GsonConverterFactory.create()) // Usar Gson para la conversión JSON
            .build()

        retrofit.create(FootballApiService::class.java)
    }
}
