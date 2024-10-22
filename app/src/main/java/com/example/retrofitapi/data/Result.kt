package com.example.retrofitapi.data

// This will handle the api response to see for errors or success
sealed class Result<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T> (data: T?): Result<T>(data)
    class Error<T> (data: T? = null, message: String): Result<T>(data,message)
}