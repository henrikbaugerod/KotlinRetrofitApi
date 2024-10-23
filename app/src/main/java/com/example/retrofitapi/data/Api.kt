package com.example.retrofitapi.data

import com.example.retrofitapi.data.model.Items
import com.example.retrofitapi.data.model.LoginRequest
import com.example.retrofitapi.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// This stores the main API function that runs and gets the data.
// You can also pass queries as parameters in the getItemList if needed
interface Api {
    @GET("items")
    suspend fun getItemList(): Items

    @POST(".")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    companion object {
        const val BASE_URL = "https://biljard.catchmedia.no/api2/"
    }
}