package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.runtime.Composable
import retrofit2.http.GET

data class UserDto(
    val username: String,
    val password: String
)

interface UserApi {
    @GET("/")
    suspend fun getUsers(): List<UserDto>
}


@Composable
fun LoginPage() {

}