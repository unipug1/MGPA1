package com.nyp.scaffoldingapp.ui.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed

@Composable
private fun RankingsPage(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    userYearOfStudy: String = "Poly & ITE",
    currentUserID: String = "2",
    viewModel: RankingsViewModel
) {
    val studyRecords by viewModel.studyRecords.collectAsState()

    val categoryOptions = listOf("All", userYearOfStudy)
    // default value: All
    var selectedCategory by rememberSaveable { mutableStateOf(categoryOptions[0]) }
    var categoryExpanded by rememberSaveable { mutableStateOf(false) }

    val periodOptions = listOf("Day", "Week", "Month")
    var selectedPeriod by rememberSaveable { mutableStateOf(periodOptions[0]) }

    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    val currentDate = LocalDate.parse(selectedDate)


    val periodStart = when (selectedPeriod) {
        "Week" -> currentDate.with(
            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        )
        "Month" -> currentDate.withDayOfMonth(1)
        else -> currentDate
    }

    val periodEnd = when (selectedPeriod) {
        "Week" -> periodStart.plusDays(6)
        "Month" -> periodStart.plusMonths(1).minusDays(1)
        else -> currentDate
    }

    val filteredRecords = studyRecords.filter { record ->
        val recordDate = LocalDate.parse(record.date)

        !recordDate.isBefore(periodStart) &&
                !recordDate.isAfter(periodEnd) &&
                (selectedCategory == "All" ||
                        record.yearOfStudy == selectedCategory)
    }

    val rankings = filteredRecords
        .groupBy { it.userID }
        .map { (_, records) ->
            val user = records.first()

            user.copy(
                durationSeconds = records.sumOf { it.durationSeconds }
            )
        }
        .sortedByDescending { it.durationSeconds }

    val todayCount = rankings.size
    val myRankIndex = rankings.indexOfFirst { record ->
        record.userID == currentUserID
    }
    val myRank = if (myRankIndex >= 0) {
        myRankIndex + 1
    } else {
        null
    }
    val topPercentage = if (myRank != null && todayCount > 0) {
        (myRank * 100 + todayCount - 1) / todayCount
    } else {
        null
    }

    val dateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)
    val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH)

    val dateText = when (selectedPeriod) {
        "Day" -> currentDate.format(dateFormatter)
        "Week" -> {
            val weekStart = currentDate.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
            )
            val weekEnd = weekStart.plusDays(6)
            "${weekStart.format(dateFormatter)} ~ ${weekEnd.format(dateFormatter)}"
        }
        "Month" -> currentDate.format(monthFormatter)
        else -> ""
    }

    Column(modifier = modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        Box(modifier = Modifier.fillMaxWidth()
            .height(56.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("<")
            }
            Text(
                text = "Total rankings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Center)
            )
            Box(modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Row(modifier = Modifier.clickable{ categoryExpanded = true }
                    .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = selectedCategory,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "▼",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categoryOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedCategory = option
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()
            .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            periodOptions.forEach { option ->
                val isSelected = selectedPeriod == option

                Column(modifier = Modifier.weight(1f)
                    .clickable{ selectedPeriod = option },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onBackground
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isSelected) {
                        Box(modifier = Modifier.width(26.dp)
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.onBackground)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val previousDate = when (selectedPeriod) {
                        "Day" -> currentDate.minusDays(1)
                        "Week" -> currentDate.minusWeeks(1)
                        "Month" -> currentDate.minusMonths(1)
                        else -> currentDate
                    }
                    selectedDate = previousDate.toString()
                }
            ) {
                Text("<")
            }
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = {
                    val nextDate = when (selectedPeriod) {
                        "Day" -> currentDate.plusDays(1)
                        "Week" -> currentDate.plusWeeks(1)
                        "Month" -> currentDate.plusMonths(1)
                        else -> currentDate
                    }
                    selectedDate = nextDate.toString()
                }
            ) {
                Text(">")
            }
        }
        if (selectedPeriod == "Day") {
            Row(modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$todayCount",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column {
                    Text(
                        text = "My rank",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = myRank?.toString() ?: "-",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column {
                    Text(
                        text = "Top",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = topPercentage?.let { "$it%" } ?: "-",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            if (rankings.isEmpty()) {
                item {
                    Text(
                        text = "No study records for this period",
                        modifier = Modifier.padding(vertical = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                itemsIndexed(
                    items = rankings,
                    key = { _, record -> record.userID }
                ) { index, record ->
                    RankingRow(
                        rank = index + 1,
                        nickname = record.userName,
                        yearOfStudy = record.yearOfStudy,
                        studyTimeSeconds = record.durationSeconds,
                        dailyGoalSeconds = rankings.first().durationSeconds
                    )
                }
            }
        }

    }
}

@Composable
private fun RankingRow(
    rank: Int,
    nickname: String,
    yearOfStudy: String,
    studyTimeSeconds: Long,
    dailyGoalSeconds: Long
) {
    val progress = if (dailyGoalSeconds > 0L) {
        (studyTimeSeconds.toFloat() / dailyGoalSeconds).coerceIn(0f, 1f)
    } else { 0F }

    Row(modifier = Modifier.fillMaxWidth()
        .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // rank badge
        Surface(modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        // member details
        Column(modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = yearOfStudy,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = nickname,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = String.format(
                        Locale.US,
                        "%d:%02d:%02d",
                        studyTimeSeconds / 3600,
                        (studyTimeSeconds % 3600) / 60,
                        studyTimeSeconds % 60
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun MockRankingsPagePreview() {
    MaterialTheme { RankingsPage(Modifier, { }, "Poly & ITE",
        viewModel = RankingsViewModel()
    )}
}