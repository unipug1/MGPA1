package com.nyp.scaffoldingapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object UserApi {
    private const val ENDPOINT = "https://1kc618.mockapi.dog/"
    private const val SIGNUP_ENDPOINT = "https://1kc618.mockapi.dog/accounts/"

    private const val TIMEOUT_MS = 10_000
    suspend fun fetchUser(email : String, password : String): User = withContext(Dispatchers.IO) {
        val connection = (URL(ENDPOINT).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
        }
        try {
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            parseUser(body, email, password)
        } finally {
            connection.disconnect()
        }
    }

    suspend fun createAccount() : Boolean = withContext(Dispatchers.IO){
        val connection = (URL(SIGNUP_ENDPOINT).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
        }
        try {
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            parseSuccess(body)
        } finally {
            connection.disconnect()
        }
    }

    private fun parseUser(body : String, email: String, password : String) : User {
        val json = JSONObject(body)
        //val user = json.getJSONObject(email)
        return User(
            json.getString("name"),
            email,
            password,
            id = json.getString("id")
        )
    }
    private fun parseSuccess(body: String) : Boolean{
        val json = JSONObject(body)
        return json.getBoolean("success")
    }
}

