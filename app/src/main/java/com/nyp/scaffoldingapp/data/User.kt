package com.nyp.scaffoldingapp.data



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


import kotlinx.coroutines.flow.Flow
import java.util.UUID

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