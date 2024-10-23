package com.example.retrofitapi.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.retrofitapi.RetrofitInstance
import kotlinx.coroutines.launch
import com.example.retrofitapi.data.model.LoginRequest

@Composable
fun LoginPage(
    onLoginSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Remember state for username and password input fields
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Username input field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password input field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = {
                coroutineScope.launch {
                    val loginRequest = LoginRequest(
                        username = username,
                        password = password
                    )
                    try {
                        val response = RetrofitInstance.api.login(loginRequest)
                        onLoginSuccess(response.message) // Callback on successful login
                    } catch (e: Exception) {
                        Toast.makeText(context, "Login Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ) {
            Text(text = "Login")
        }
    }
}
