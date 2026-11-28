package com.nyp.scaffoldingapp.ui.user

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nyp.scaffoldingapp.data.UserViewModel


enum class AccountPage{
    DEFAULT,
    SIGN_UP,
    SIGN_IN
}

@Composable
fun SignInPage(viewModel: UserViewModel, onDismiss : () ->Unit, onSignIn : ()->Unit){
    var email = rememberSaveable() { mutableStateOf("") }
    var password = rememberSaveable() { mutableStateOf("") }

    val user by viewModel.GetUserFlow().collectAsStateWithLifecycle(initialValue = null)

    val error by viewModel.GetErrorFlow().collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = {
            IconButton(
                onClick = { onDismiss() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)){
            OutlinedTextField(
                value = email.value,
                label = {
                    Text("Email")
                },
                onValueChange = { email.value = it},
                singleLine = true
            )

            OutlinedTextField(
                value = password.value,
                label = {
                    Text("Password")
                },
                onValueChange = { password.value = it},
                singleLine = true
            )
            Button(onClick = {
                viewModel.signIn(email.value, password.value)

            }){
                Text("Sign In")
            }
            if (error != null) Text(error!!, color = Color.Red)
        }
    }
    LaunchedEffect(user) {
        if (user != null) {
            onSignIn()
            onDismiss()
            Log.d("Signin", viewModel.GetUser()!!.name)
        }
    }
}

@Composable
fun SignUpPage(viewModel: UserViewModel, onDismiss : () ->Unit){
    var email = rememberSaveable() { mutableStateOf("") }
    var password = rememberSaveable() { mutableStateOf("") }

    val success by viewModel.GetSuccessFlow().collectAsStateWithLifecycle(initialValue = null)

    val error by viewModel.GetErrorFlow().collectAsStateWithLifecycle(initialValue = null)


    Scaffold(
        topBar = {
            IconButton(
                onClick = { onDismiss() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = email.value,
                label = {
                    Text("Email")
                },
                onValueChange = { email.value = it },
                singleLine = true
            )

            OutlinedTextField(
                value = password.value,
                label = {
                    Text("Password")
                },
                onValueChange = { password.value = it },
                singleLine = true
            )
            Button(onClick = {
                viewModel.SignUp()

            }) {
                Text("Sign Up")
            }
            if (error != null) Text(error!!, color = Color.Red)
            if (success != null) {
                Text(success!!, color = Color.Green)

            }
        }
    }
}

@Composable
fun LoginPage(viewModel : UserViewModel, onSignIn: () -> Unit) {
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
        AccountPage.SIGN_UP ->{
            SignUpPage(viewModel) { page.value = AccountPage.DEFAULT }
        }
        AccountPage.SIGN_IN ->{SignInPage(viewModel, { page.value = AccountPage.DEFAULT}, {
            onSignIn()
        })}
    }
}