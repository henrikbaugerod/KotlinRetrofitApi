package com.example.retrofitapi.data

import com.example.retrofitapi.data.model.Items
import retrofit2.http.GET

// This stores the main API function that runs and gets the data.
// You can also pass queries as parameters in the getItemList if needed
interface Api {
    @GET("items")
    suspend fun getItemList(): Items

    companion object {
        const val BASE_URL = "https://your.api.com/"
    }
}