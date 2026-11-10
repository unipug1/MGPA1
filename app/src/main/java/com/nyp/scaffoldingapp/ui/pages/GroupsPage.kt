package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.core.graphics.toColorInt
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import kotlinx.coroutines.selects.select

@Composable
fun GroupsPage(modifier: Modifier = Modifier) {
    Surface(Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxWidth()) {
            Row(Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text("My groups")
                Spacer(Modifier.weight(1f))
                Button(onClick = {println("Notifications")}) {
                    Text("Bell")
                }
            }



            Spacer(Modifier.weight(1f))

            Button(onClick = {println("Add")}) {
                Text("+")
            }
        }
    }
}

// test out adding/joining group details feature
@Composable
fun GroupsDiscoverPage(modifier: Modifier = Modifier) {
    var selectedFilter by rememberSaveable {mutableStateOf("New")}
    val filters = listOf("New", "Attendance", "Study group")
    var yearChecked by rememberSaveable {mutableStateOf(false)}
    var availableChecked by rememberSaveable {mutableStateOf(false)}
    var publicChecked by rememberSaveable {mutableStateOf(false)}

    Surface(Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background) {
        // back button + title + search button
        Row(Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Button(onClick = {println("Back button clicked")}) {
                Text("<")
            }
            Text("Study group", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = {println("Search button clicked")}) {
                Text("Search")
            }
        }

        Column(Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            // filter row
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(filter)
                        }
                    )
                }
            }

            HorizontalDivider(thickness = 5.dp)

            // filter row checkboxes
            Column(Modifier.fillMaxWidth()) {
                // change based on new, attendance, study group
                Text("*Groups are sorted by creation date")

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // year
                    Checkbox(
                        checked = yearChecked,
                        onCheckedChange = {yearChecked = it}
                    )
                    // dynamic
                    Text("J2")

                    // available
                    Checkbox(
                        checked = availableChecked,
                        onCheckedChange = {availableChecked = it}
                    )
                    Text("Available")

                    // public
                    Checkbox(
                        checked = publicChecked,
                        onCheckedChange = {publicChecked = it}
                    )
                    Text("Public")
                }

                // sorted output
            }

            Spacer(Modifier.weight(1f))

            // button to create group
            Button(onClick = {println("create group")}) {
                Text("+")
            }
        }
    }
}

// test for creating group
@Composable
fun CreateGroup(modifier: Modifier = Modifier) {
    Surface(Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background) {
        // back button + title
        Row(Modifier.fillMaxWidth()) {

        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
private fun MockGroupsPagePreview() {
    MaterialTheme { GroupsPage(Modifier) }
}