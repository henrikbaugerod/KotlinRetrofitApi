## 1. Add implementations in build.gradle.kts (Module :app)

Add retrofit

```bash
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
```

Add coil for images
```bash
implementation("io.coil-kt:coil-compose:2.4.0")
```

## 2. Add internet connection to the Android Manifest
Add this line of code inside the <manifest>
```bash
<uses-permission android:name="android.permission.INTERNET" />
```

## 3. Build this package structure
```bash
your_app_name/
├─ data/
│  ├─ model/
│  │  ├─ Item
│  │  ├─ Items
│  ├─ Api
│  ├─ ItemRepository
│  ├─ ItemRepositoryImpl
│  ├─ Result
├─ presentation/
│  ├─ ItemViewModel
├─ ui/
├─ MainActivity.kt
├─ RetrofitInstance
```

## 4. Create your Item and Items data classes inside data/model/
Items will contain the total and an array of Item objects.
```bash
package com.example.retrofitapi.data.model

data class Items(
    var total: Int,
    var items: List<Item>
)

```
```bash
package com.example.retrofitapi.data.model

data class Item(
    val id: Int,
    val title: String,
    val description: String
)
```

## 5. Create your Api Interface
This will store the API_URL and the function to call the API_URL.
```bash
package com.example.retrofitapi.data

import com.example.retrofitapi.data.model.Item
import retrofit2.http.GET

interface Api {
    @GET("items")
    suspend fun getItemList(): Items

    companion object {
        const val BASE_URL = "https://your.api.no/"
    }
}
```

You can add more @GET('newItems') to get other objects. You can also pass in queries as parameters to the function to filter more.
```bash
package com.example.retrofitapi.data

import com.example.retrofitapi.data.model.Item
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("items/{type}")
    suspend fun getItemList(
        @Path("type") type: String,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): Items

    @GET("customers")
    suspend fun getCustomerList(): Customers

    companion object {
        const val BASE_URL = "https://your.api.no/"
    }
}
```

## 6. Create your Result.kt to handle the API response
```bash
package com.example.retrofitapi.data

sealed class Result<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T> (data: T?): Result<T>(data)
    class Error<T> (data: T? = null, message: String): Result<T>(data,message)
}
```

## 7. Create an Interface to get all items from the Item array
```bash
package com.example.retrofitapi.data

import com.example.retrofitapi.data.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    suspend fun getItemList(): Flow<Result<List<Item>>>
}
```

## 8. Create an Implementation Class
This class implements the ItemRepository interface and contains the actual logic for fetching the data.
```bash
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
```

## 9. Create your viewmodel in the presentation package
```bash
package com.example.retrofitapi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofitapi.data.ItemRepository
import com.example.retrofitapi.data.Result
import com.example.retrofitapi.data.model.Item
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemViewModel(
    private val itemRepository: ItemRepository
): ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items = _items.asStateFlow()

    private val _showErrorToastChannel = Channel<Boolean>()
    val showErrorToastChannel = _showErrorToastChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            itemRepository.getItemList().collectLatest { result ->
                when(result) {
                    is Result.Error -> {
                        _showErrorToastChannel.send(true)
                    }
                    is Result.Success -> {
                        result.data?.let { items ->
                            _items.update { items }
                        }
                    }
                }
            }
        }
    }
}
```

## 10. Create a RetrofitInstance
This is a singleton (only one) object that provides a centralized way to configure and create a Retrofit instance for making API calls.
```bash
package com.example.retrofitapi

import com.example.retrofitapi.data.Api
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    val api: Api = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Api.BASE_URL)
        .client(client)
        .build()
        .create(Api::class.java)
}
```

## 11. Setup a ViewModel Factory in MainActivity
This is an interface that allows you to create instances of an Item ViewModel with custom parameters.
```bash
private val viewModel by viewModels<ItemViewModel>(factoryProducer = {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
                return ItemViewModel(ItemRepositoryImpl(RetrofitInstance.api)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
})
```

## 12. Finally create your UI to display the fetched data
```bash
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetrofitApiTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Collects the products flow from the viewModel, converting it to a state that can be used in the UI.
                    val itemList = viewModel.items.collectAsState().value
                    val context = LocalContext.current

                    // This is a composable that runs a coroutine when the key changes (in this case, when showErrorToastChannel changes).
                    LaunchedEffect(key1 = viewModel.showErrorToastChannel) {
                        viewModel.showErrorToastChannel.collectLatest { show ->
                            if (show) {
                                Toast.makeText(
                                    context, "Error", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    if (itemList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(itemList.size) { index ->
                                Item(itemList[index])
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
```

```bash
@Composable
fun Item(item: Item) {
    Row {
        Spacer(Modifier.height(20.dp))
        Text(
            text = "${item.id}: ${item.title} - ${item.description}"
        )
    }
}
```

