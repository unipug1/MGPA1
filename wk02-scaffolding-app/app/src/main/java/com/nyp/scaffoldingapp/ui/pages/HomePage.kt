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


/*
* Page 1 is the home page
*/


@Composable
fun HomePage(modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSubjectDialogue by rememberSaveable {mutableStateOf(false)}


    Surface(
        Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when(val state = uiState) {
            is HomeUIState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is HomeUIState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is HomeUIState.Success -> {
                Column(Modifier.fillMaxSize()
                    .padding(top = 16.dp)
                ) {
                    // first section: d-date, actual day + date, more button
                    Box(Modifier.fillMaxWidth()
                        .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center)
                    {
                        Card(Modifier.align(Alignment.CenterStart),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text("D-Day", Modifier.padding(10.dp),
                                style = MaterialTheme.typography.bodyMedium)
                        }
                        Text("Fri, 10/30", style = MaterialTheme.typography.bodyMedium)
                        Text("□", Modifier.align(Alignment.CenterEnd),
                            style = MaterialTheme.typography.headlineMedium)
                    }


                    Spacer(Modifier.height(20.dp))


                    Card(Modifier.fillMaxWidth(),
                        shape = RectangleShape,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.fillMaxWidth()
                            .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally)
                        {
                            // focus timer section
                            val hours = state.currentFocusTime / 3600
                            val minutes = (state.currentFocusTime % 3600) / 60
                            val seconds = state.currentFocusTime % 60
                            val formattedTime = String.format("%d:%02d:%02d", hours, minutes, seconds)
                            Text(text = formattedTime, style = MaterialTheme.typography.headlineLarge)


                            Spacer(Modifier.height(20.dp))


                            val restHours = state.currentRestTime / 3600
                            val restMinutes = (state.currentRestTime % 3600) / 60
                            val restSeconds = state.currentRestTime % 60
                            val restFormattedTime = "${restHours}h${restMinutes}min${restSeconds}s"
                            Text(text = restFormattedTime, style = MaterialTheme.typography.headlineSmall)
                        }
                    }


                    HorizontalDivider(thickness = 2.dp)
                    // subject list section
                    SubjectListSection(subjects = state.subjects,
                        onSubjectClick =  {selectedSubject ->
                            // handle subject selection or navigation to timer tracking
                        }
                    )


                    Spacer(Modifier.weight(1f))
                    // add subject button
                    Button(onClick = {showAddSubjectDialogue = true},
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant // ensures text is readable
                        ),
                        modifier = Modifier.padding(start = 15.dp)
                    ) {
                        Text("Add Subject")
                    }
                    Spacer(Modifier.padding(5.dp))
                }
            }
        }
    }


    if (showAddSubjectDialogue) {
        AddSubjectDialog(
            onDismiss = {showAddSubjectDialogue = false},
            onSave = {name, color ->
                viewModel.addSubject(name, color)
                showAddSubjectDialogue = false
            }
        )
    }
}


@Composable
fun SubjectListSection(
    subjects: List<Subject>,
    onSubjectClick: (Subject) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(subjects, key = { it.id }) { subject ->
            SubjectRowItem(subject = subject) {
                onSubjectClick(subject)
            }
        }
    }
}


@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var subjectName by rememberSaveable {mutableStateOf("")}
    val subjectNameFocusRequester = remember { FocusRequester() }
    // default color grey
    var subjectColor by rememberSaveable {mutableStateOf("#9E9E9E")}
    var showSelectColorDialogue by rememberSaveable {mutableStateOf(false)}


    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = onDismiss) {
                        Text("✗")
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Subject name",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.weight(1f))
                    Button(onClick = { onSave(subjectName.trim(), subjectColor) },
                        enabled = subjectName.isNotBlank()
                    ) {
                        Text("✓")
                    }
                }


                // subject name
                Spacer(Modifier.height(30.dp))
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = {subjectName = it},
                        modifier = Modifier.fillMaxWidth()
                            .focusRequester(subjectNameFocusRequester),
                        placeholder = {Text("e.g. math, english, science, geography...")},
                        singleLine = true
                    )
                    LaunchedEffect(Unit) {
                        subjectNameFocusRequester.requestFocus()
                    }
                }


                // subject color
                Spacer(Modifier.padding(30.dp))
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Select Color")


                        val parsedColor = try {
                            Color(subjectColor.toColorInt())
                        } catch (e:Exception) {
                            Color.Gray
                        }
                        // color wheel
                        Box(modifier = Modifier
                            .size(24.dp)
                            .background(color = parsedColor, shape = CircleShape)
                            .clickable{ showSelectColorDialogue = true}
                        )
                    }
                }
            }
        }
    }


    // add more colors later
    val availableColors = listOf(
        "#EA1E63", // red pink
        "#FD9800", // light orange
        "#FD5723", // orange
        "#FFEB3C", // pale yellow
        "#7A574D", // brown
        "#9E9E9E" // gray
    )


    if (showSelectColorDialogue) {
        var tempSelectedColor by rememberSaveable {mutableStateOf(subjectColor)}


        Dialog(onDismissRequest = onDismiss) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ) {
                    Spacer(Modifier.height(16.dp))
                    Text("Select color", style = MaterialTheme.typography.headlineSmall)


                    // color selection
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(availableColors) { colorHex ->
                            val colorInt = try {
                                Color(colorHex.toColorInt())
                            } catch (e: Exception) {
                                Color.Gray
                            }


                            Box(Modifier
                                .size(40.dp)
                                .background(colorInt, shape = CircleShape)
                                .clickable { tempSelectedColor = colorHex},
                                contentAlignment = Alignment.Center
                            ) {
                                if (colorHex == tempSelectedColor) {
                                    Text("✓", color = Color.Black)
                                }
                            }
                        }
                    }


                    // buttons
                    Row(Modifier.fillMaxWidth()){
                        // 1: cancel
                        Button(onClick = {showSelectColorDialogue = false},
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant // ensures text is readable
                            )) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.weight(1f))
                        // 2: ok
                        Button(onClick = {subjectColor = tempSelectedColor
                            showSelectColorDialogue = false},
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant // ensures text is readable
                            )) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SubjectRowItem(subject: Subject, onClick: () -> Unit) {
    val subjectColor = try {
        Color(subject.colorHex.toColorInt())
    }
    catch (e: Exception) {
        // replace with material theme
        Color.Gray
    }


    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick( )},
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 0.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // left group: play button + subject name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // play circle button thing
                Box(modifier = Modifier
                    .size(16.dp)
                    .background(subjectColor, shape = CircleShape)
                )
                Text(text = subject.name, style = MaterialTheme.typography.bodyLarge)
            }


            // right group
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val hours = subject.timeSpentSeconds / 3600
                val minutes = (subject.timeSpentSeconds % 3600) / 60
                val seconds = subject.timeSpentSeconds % 60
                val formattedTime = String.format("%d:%02d:%02d", hours, minutes, seconds)


                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodyLarge
                )


                // edit subject button
                Button(onClick = {println("Button clicked!")},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Text(
                        text = "⋮",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


// Preview
@Preview(showBackground = true)
@Composable
private fun MockPage1Preview() {
    val viewModel = HomeViewModel()
    MaterialTheme { HomePage(Modifier, viewModel) }
}
