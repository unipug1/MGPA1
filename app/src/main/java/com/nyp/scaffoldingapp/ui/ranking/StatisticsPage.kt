package com.nyp.scaffoldingapp.ui.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.style.TextAlign
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import java.time.temporal.ChronoUnit

@Composable
private fun StatisticsPage(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    var selectedMonth by rememberSaveable {
        mutableStateOf(LocalDate.now().withDayOfMonth(1).toString())
    }
    var selectedPeriod by rememberSaveable {
        mutableStateOf("Day")
    }

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

    val dateFormatter = DateTimeFormatter.ofPattern(
        "EEE, MMM d",
        Locale.ENGLISH
    )
    val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH)

    val dateText = when (selectedPeriod) {
        "Day" -> currentDate.format(dateFormatter)
        "Week" -> "${periodStart.format(dateFormatter)} ~ ${periodEnd.format(dateFormatter)}"
        "Month" -> currentDate.format(monthFormatter)
        else -> ""
    }

    val currentMonth = LocalDate.parse(selectedMonth)

    val calendarStart = currentMonth.with(
        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
    )
    val calendarDates = (0 until 42).map { dayOffset ->
        calendarStart.plusDays(dayOffset.toLong())
    }
    val weekdays = listOf(
        "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    )
    val periodOptions = listOf(
        "Day", "Week", "Month"
    )
    val studyTimeByDate = mapOf(
        "2026-11-03" to 3900L,
        "2026-11-05" to 21180L,
        "2026-11-06" to 32580L,
        "2026-11-07" to 25080L,
        "2026-11-16" to 540L,
        "2026-11-19" to 2L
    )
    val filteredStudyTimes = studyTimeByDate.filter { (date, _) ->
        val studyDate = LocalDate.parse(date)

        !studyDate.isBefore(periodStart) &&
                !studyDate.isAfter(periodEnd)
    }
    val totalStudySeconds = filteredStudyTimes.values.sum()
    val totalStudyTimeFullText = String.format(
        Locale.US,
        "%d:%02d:%02d",
        totalStudySeconds / 3600,
        (totalStudySeconds % 3600) / 60,
        totalStudySeconds % 60
    )
    val numberOfDays = ChronoUnit.DAYS.between(
        periodStart,
        periodEnd
    ) + 1
    val averageStudySeconds = totalStudySeconds / numberOfDays
    val averageStudyTimeText = String.format(
        Locale.US,
        "%d:%02d",
        averageStudySeconds / 3600,
        (averageStudySeconds % 3600) / 60
    )

    Column(modifier = modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState())
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
                text = "Statistics",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("Profile")
                }
            }
            Column {
                Text(
                    text = "JC Year 2",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Student A",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        Text(
            text = "Joined on Oct 29, 2026",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()
            .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    selectedMonth = currentMonth.minusMonths(1).toString()
                }
            ) {
                Text("<")
            }
            Text(
                text = currentMonth.format(monthFormatter),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = {
                    selectedMonth = currentMonth.plusMonths(1).toString()
                }
            ) {
                Text(">")
            }
        }
        Text(
            text = "Focus time",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
        ) {
            weekdays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
            calendarDates.chunked(7).forEach { weekDates ->
                Row(modifier = Modifier.fillMaxWidth()
                ) {
                    weekDates.forEach { date ->
                        val studySeconds = studyTimeByDate[date.toString()] ?: 0L
                        val studyHours = studySeconds / 3600f
                        val backgroundAlpha = when {
                            studyHours >= 8f -> 0.35f
                            studyHours >= 4f -> 0.25f
                            studyHours >= 1f -> 0.15f
                            studySeconds > 0L -> 0.08f
                            else -> 0f
                        }
                        val isSelectedDate = date == currentDate

                        Box(modifier = Modifier.weight(1f)
                            .height(64.dp)
                            .background(
                                MaterialTheme.colorScheme.onSurface.
                                copy(alpha = backgroundAlpha))
                            .clickable {
                                selectedDate = date.toString()
                            }
                            .border(
                                width = if (isSelectedDate) 2.dp else 0.dp,
                                color = if (isSelectedDate) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.background
                                }
                            ),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(modifier = Modifier.padding(top = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    // if date outside selected month = lighter
                                    color = if (date.month == currentMonth.month &&
                                        date.year == currentMonth.year) {
                                        MaterialTheme.colorScheme.onBackground
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    }
                                )
                                if (studySeconds > 0L) {
                                    Text(
                                        text = String.format(
                                            Locale.US,
                                            "%d:%02d",
                                            studySeconds / 3600,
                                            (studySeconds % 3600) / 60
                                        ),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
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
        Text(
            text = dateText,
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (selectedPeriod == "Day") {
            Card(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column (modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Start",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "--",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Column(modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "End",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "--",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Max focus",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "--",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Column(modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Allowed Apps",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "0:00:00",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
        if (selectedPeriod == "Week" || selectedPeriod == "Month") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Total study time",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = totalStudyTimeFullText,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Avg study time",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = averageStudyTimeText,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Study averages",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Avg study",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = averageStudyTimeText,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Avg start",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "--",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Avg end",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "--",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
private fun MockStatisticsPagePreview() {
    MaterialTheme { StatisticsPage(Modifier, {}) }
}