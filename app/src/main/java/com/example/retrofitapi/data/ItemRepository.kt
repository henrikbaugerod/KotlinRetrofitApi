package com.example.retrofitapi.data

import com.example.retrofitapi.data.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    suspend fun getItemList(): Flow<Result<List<Item>>>
}