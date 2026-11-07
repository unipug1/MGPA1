package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/*
* Page 1 is the home page
 */

@Composable
fun HomePage(modifier: Modifier = Modifier) {

}

// Preview
@Preview(showBackground = true)
@Composable
private fun MockPage1Preview() {
    MaterialTheme { HomePage() }
}