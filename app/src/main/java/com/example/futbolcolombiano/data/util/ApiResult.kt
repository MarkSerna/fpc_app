package com.example.futbolcolombiano.data.util

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    // Podríamos añadir un estado Loading, pero usualmente se maneja con un LiveData separado en el ViewModel
    // object Loading : ApiResult<Nothing>()
}
