package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.nyp.scaffoldingapp.ui.KaiserClasses.MusicViewModel
import com.nyp.scaffoldingapp.ui.KaiserClasses.User
import com.nyp.scaffoldingapp.ui.KaiserClasses.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.http.GET
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID





object UserApi { // the data layer: the only code that touches the network
    private const val ENDPOINT = "https://1kc618.mockapi.dog"
    private const val SIGNUP_ENDPOINT = "https://1kc618.mockapi.dog/accounts/"

    private const val TIMEOUT_MS = 10_000
    suspend fun fetchUser(email : String): User = withContext(Dispatchers.IO) {
        val connection = (URL(ENDPOINT).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
        }
        try {
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            parseUser(body, email)
        } finally {
            connection.disconnect()
        }
    }

    suspend fun createAccount() : Boolean{
        val connection = (URL(SIGNUP_ENDPOINT).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
        }
        try {
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            return parseSuccess(body)
        } finally {
            connection.disconnect()
        }
    }

    private fun parseUser(body : String, email: String) : User{
        val json = JSONObject(body)
        val user = json.getJSONObject(email)
        return User(
            user.getString("name"),
            email,
            user.getString("password"),
            user.getString("id")
        )
    }
    private fun parseSuccess(body: String) : Boolean{
        val json = JSONObject(body)
        return json.getBoolean("success")
    }
}

enum class AccountPage{
    DEFAULT,
    SIGN_UP,
    SIGN_IN
}

@Composable
fun SignUpPage(viewModel: UserViewModel){
    var email = rememberSaveable() { mutableStateOf("") }
    var password = rememberSaveable() { mutableStateOf("") }
    Scaffold() { paddingValues ->
        Column(Modifier.padding(paddingValues)){
            Button(onClick = {
                val userObj = viewModel.signIn(email.value)
            }){

            }
        }
    }
}

@Composable
fun LoginPage(viewModel : UserViewModel) {
    val page = rememberSaveable() { mutableStateOf(AccountPage.DEFAULT) }
    when (page.value) {
        AccountPage.DEFAULT -> Column() {
            Spacer(Modifier.weight(1f))
            Button(onClick = { page.value = AccountPage.SIGN_UP }) {
                Text("Sign up")
            }
            Button(onClick = { page.value = AccountPage.SIGN_IN }) {
                Text("Sign in")
            }
        }
        AccountPage.SIGN_UP ->{}
        AccountPage.SIGN_IN ->{}
    }
}