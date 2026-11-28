package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nyp.scaffoldingapp.ui.KaiserClasses.UserCategory
import com.nyp.scaffoldingapp.ui.KaiserClasses.UserViewModel
import com.nyp.scaffoldingapp.ui.KaiserClasses.User

@Composable
fun CategoryPage(viewModel: UserViewModel, onDismiss : ()->Unit, onSelect : (UserCategory)->Unit){
    val user = viewModel.user.collectAsStateWithLifecycle()

    val selectedCategory = rememberSaveable() { mutableStateOf(user.value!!.userCategory) }
    Scaffold(
        topBar = {
            Column() {
                IconButton(
                    onClick = { onDismiss() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Spacer(Modifier.weight(1f))

            }

        }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                for (category in UserCategory.entries) {
                    if (category.name.contains("TYPE_")) {
                        Text(category.categoryName)
                    }
                    else {
                        Button(onClick = {selectedCategory.value = category}, colors = ButtonDefaults.buttonColors( containerColor = if (selectedCategory.value == category) Color.Yellow else Color.LightGray)){
                            Text(category.categoryName)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NamePage(user : User,onDismiss: () -> Unit, onSelect : (String)->Unit){

    val nameSizeLimit = 60

    var name = rememberSaveable() { mutableStateOf(user.name) }
    Scaffold(modifier = Modifier.padding(15.dp),
        topBar = {
            Row(Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { onDismiss() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Spacer(Modifier.weight(1f))
                Text("Change nickname")
                Spacer(Modifier.weight(1f))
                Button(onClick ={
                    onDismiss()
                    onSelect(name.value)
                }){
                    Text("Done")
                }
            }
        }
    ){ paddingValues ->
        Column(Modifier.padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally){
            OutlinedTextField(
                value = name.value,
                onValueChange = {name.value = it},
                label = { Text("Name") }
            )
            Spacer(Modifier.height(10.dp))
            Text("${name.value.length} / ${nameSizeLimit}")
        }
    }
}

enum class SettingOpen{
    DEFAULT,
    NAME,
    CATEGORY
}

@Composable
fun SettingsPage(viewModel: UserViewModel) {

}