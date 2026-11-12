package com.nyp.scaffoldingapp.ui.pages

import android.widget.Space
import android.widget.TimePicker
import java.time.Instant
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nyp.scaffoldingapp.ui.KaiserClasses.ToDoTask
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.DayOfWeek

import com.nyp.scaffoldingapp.ui.KaiserClasses.Subject
import com.nyp.scaffoldingapp.ui.KaiserClasses.Task
import com.nyp.scaffoldingapp.ui.KaiserClasses.DateTimeFormat
import kotlinx.coroutines.delay

import kotlin.collections.listOf
import kotlin.time.Duration.Companion.milliseconds

import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyp.scaffoldingapp.ui.KaiserClasses.SubjectViewModel
import com.nyp.scaffoldingapp.ui.KaiserClasses.ToDoSubjects
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale
import java.util.Date
import kotlin.time.ExperimentalTime

@Composable
fun CalendarPage(viewModel: SubjectViewModel, modifier: Modifier){

    val datePickerState = rememberDatePickerState()

    var pickedDate: String = ""
    datePickerState.selectedDateMillis?.let{ pickedDate = ConvertMilisToDate(it)}
    Column(Modifier.fillMaxWidth().fillMaxHeight()){
        OutlinedTextField(
            value = pickedDate,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
        )
        DatePicker(state = datePickerState, showModeToggle = false )
    }
}