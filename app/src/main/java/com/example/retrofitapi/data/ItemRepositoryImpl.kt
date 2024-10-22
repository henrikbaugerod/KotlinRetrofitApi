package com.example.retrofitapi.data

import android.util.Log
import com.example.retrofitapi.data.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ItemRepositoryImpl(
    private val api: Api
): ItemRepository {
    override suspend fun getItemList(): Flow<Result<List<Item>>> {
        return flow {
            val itemsFromApi = try {
                api.getItemList()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ItemRepository", "Exception: Error loading items", e);
                emit(Result.Error(message = "Error loading items"))
                return@flow
            }

            emit(Result.Success(itemsFromApi.items))
        }
    }
}