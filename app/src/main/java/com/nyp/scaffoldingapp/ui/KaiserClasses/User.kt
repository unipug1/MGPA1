package com.nyp.scaffoldingapp.ui.KaiserClasses



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime


import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.LocalTime
import java.time.LocalDate

import java.text.SimpleDateFormat
import java.util.Calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


import androidx.lifecycle.viewModelScope
import com.google.common.collect.SetMultimap
import com.nyp.scaffoldingapp.R
import com.nyp.scaffoldingapp.ui.pages.HomeUIState

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjuster
import java.util.UUID
import java.time.temporal.TemporalAdjusters
import kotlin.collections.plus
import kotlin.time.Duration

enum class UserCategory(val categoryName: String){
    TYPE_SCHOOL("School Education"),
    PRIMARY_SCHOOL("Primary School & PSLE"),
    SECONDARY_SCHOOL("Secondary School, SEC & O-Level"),
    JUNIOR_COLLEGE("Junior College"),
    POLYTECHNIC("Polytechnic & ITE"),
    TYPE_UNIVERSITY("University"),
    UNIVERSITY("University"),
    TYPE_PROFESSIONAL_EXAMS("Professional Exams"),
    PROFESSIONAL_CERT("CA, ACCA, CFA, Bar * Professional certs"),
    TYPE_OTHERS("Others"),
    LANGUAGE_LEARNING("Language Learning"),
    OTHERS("Reading, self-study and other goals")
}


data class User(
    var name : String,
    var email : String,
    var password : String,
    var userCategory: UserCategory = UserCategory.OTHERS,
    var id : String = UUID.randomUUID().toString()
)



class UserViewModel : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _error.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _user.value = UserApi.fetchUser(email, password)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Sign in failed: ${e.message}"
                _user.value = null
            }
        }
    }

    fun SignUp(){
        viewModelScope.launch {
            try {
                val signUpSucc = UserApi.createAccount()
                if (signUpSucc)
                    _success.value = "Sign up successful"
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Sign in failed: ${e.message}"
                _success.value = null
            }
        }
    }

    fun GetSuccessFlow() : Flow<String?>{
        return _success
    }

    fun GetUser() : User?{
        return _user.value
    }

    fun GetUserFlow() : Flow<User?>{
        return _user
    }

    fun SetUser(user : User?){
        _user.value = user
    }

    fun GetError() : String?{
        return _error.value
    }

    fun GetErrorFlow() : Flow<String?>{
        return _error
    }
}