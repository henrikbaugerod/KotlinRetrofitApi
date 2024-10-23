package com.example.retrofitapi.data.model

data class LoginRequest(
    val action: String = "login",
    val username: String,
    val password: String
)
