package com.nyp.scaffoldingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.annotation.DrawableRes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.nyp.scaffoldingapp.ui.pages.*
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme

/**
 * The four bottom tabs. An enum keeps the list in one place — add a tab here
 * and the navigation bar updates itself.
 */
enum class Tab(val label: String, @DrawableRes val icon: Int) {
    PAGE1("Page 1", R.drawable.ic_home),
    PAGE2("Page 2", R.drawable.ic_email),
    PAGE3("Page 3", R.drawable.ic_list),
    PAGE4("Page 4", R.drawable.ic_star)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScaffoldingTheme {
                AppScaffold()
            }
        }
    }
}

@Composable
fun AppScaffold() {
    // rememberSaveable, not remember: the selected tab must survive a rotation.
    var selectedTab by rememberSaveable { mutableStateOf(Tab.PAGE1) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            // Icon() tints the image with the theme colour,
                            // which is what gives you selected / unselected
                            // states for free. Use Image() instead if you
                            // want the picture's own colours.
                            Icon(
                                painter = painterResource(tab.icon),
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        // Use the padding Scaffold gives you, or your content hides behind
        // the navigation bar.
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                Tab.PAGE1 -> Page1()
                Tab.PAGE2 -> Page2()
                Tab.PAGE3 -> Page3()
                Tab.PAGE4 -> Page4()
            }
        }
    }
}
