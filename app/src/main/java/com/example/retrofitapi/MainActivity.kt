package com.example.retrofitapi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.retrofitapi.data.ItemRepositoryImpl
import com.example.retrofitapi.data.model.Item
import com.example.retrofitapi.data.model.LoginRequest
import com.example.retrofitapi.presentation.ItemViewModel
import com.example.retrofitapi.screens.LoginPage
import com.example.retrofitapi.ui.theme.RetrofitApiTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    // Viewmodel Factory to create instances of Item ViewModels to use in application
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetrofitApiTheme {
                val context = LocalContext.current
                // Set LoginPage as the main screen
                LoginPage (onLoginSuccess = { token ->
                    Toast.makeText(context, token, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }
}

@Composable
fun Item(item: Item) {
    Row {
        Spacer(Modifier.height(20.dp))
        Text(
            text = "${item.id}: ${item.title} - ${item.description}"
        )
    }
}