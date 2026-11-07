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

/*
* Page 1 is the home page
 */

@Composable
fun HomePage(modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

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
                    // edit subject button
                    Button(onClick = {println("Button clicked!")},
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant // ensures text is readable
                        ),
                        modifier = Modifier.padding(start = 15.dp)
                    ) {
                        Text("Edit Subject")
                    }
                    Spacer(Modifier.padding(5.dp))
                }
            }
        }
    }
}

@Composable
fun SubjectListSection(
    subjects: List<Subject>,
    onSubjectClick: (Subject) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        items(subjects, key = { it.id }) { subject ->
            SubjectRowItem(subject = subject) {
                onSubjectClick(subject)
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
            .padding(16.dp),
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

                // Placeholder for the 3-dots settings menu icon
                Text(
                    text = "⋮",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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