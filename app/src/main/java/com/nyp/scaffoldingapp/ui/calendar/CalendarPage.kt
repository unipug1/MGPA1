package com.nyp.scaffoldingapp.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.DayOfWeek

import kotlin.collections.listOf

import com.nyp.scaffoldingapp.ui.todo.Routine
import com.nyp.scaffoldingapp.ui.todo.TimeFormat
import com.nyp.scaffoldingapp.ui.todo.DateRangeString
import com.nyp.scaffoldingapp.ui.todo.GetIntStrPair
import com.nyp.scaffoldingapp.ui.todo.RoutineDialog
import com.nyp.scaffoldingapp.ui.todo.StringObjPair
import com.nyp.scaffoldingapp.ui.todo.WheelPicker
import java.time.YearMonth

@Composable
fun CalendarDateCell(date: LocalDate, events :List<Event>, onClick : (LocalDate)->Unit){
    Button(onClick = {onClick(date)}, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), modifier = Modifier.padding(5.dp), border = BorderStroke(color = Color.Gray, width = 3.dp), shape = RoundedCornerShape(15.dp), contentPadding = PaddingValues(2.dp)){
        LazyColumn(Modifier.fillMaxHeight()){
            item(){
                Text(date.dayOfMonth.toString(), color = Color.Black)
            }

            items(events){ event->
                Box(Modifier.fillMaxWidth().background(Color.LightGray).clip(RoundedCornerShape(5.dp))){
                    Text(event.name, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun CalendarEventDisplay(
    month: YearMonth,
    viewModel : EventViewModel,
    onSelect : (LocalDate)->Unit
){
    val eventList by viewModel.events.collectAsStateWithLifecycle()
    val events = eventList.events



    var numWeeks = month.lengthOfMonth() / 7
    var dayNum = 0

    val offset = month.atDay(1).dayOfWeek
    val totalDays = month.lengthOfMonth()
    val dayEvents = remember(month, events){
        val returnEvents = HashMap<LocalDate, MutableList<Event>>()
        returnEvents.clear()


        if (month.atDay(1).dayOfWeek != DayOfWeek.MONDAY)
            numWeeks += 2

        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            events.forEach { event ->
                if (event.OnDay(date)) {
                    returnEvents.getOrPut(date) {
                        mutableListOf()
                    }.add(event)
                }
            }
        }
        returnEvents
    }
    Column() {
        Row(Modifier.fillMaxWidth()) {
            for (day in DayOfWeek.entries) {
                Text(text = day.name[0].toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }
        for (week in 1..numWeeks) {
            Row(Modifier.fillMaxWidth().weight(1f)) {
                for (day in DayOfWeek.entries) {
                    val dayNum = (week - 1) * 7 + day.value - offset.value + 1


                    if (dayNum in 1..totalDays) {
                        val date = month.atDay(dayNum)
                        Box(Modifier.weight(1f)) {
                            CalendarDateCell(
                                date,
                                dayEvents[date]?.toList() ?: listOf(),
                                { onSelect(it) })
                        }
                    }
                    else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }

}





fun DateRangeStrPair(startDate : LocalDate, endDate : LocalDate, format : String = "EEE, MM-dd-yyyy") : MutableList<StringObjPair<LocalDate>>{
    val dateList = mutableListOf<StringObjPair<LocalDate>>()
    var currentDate = startDate
    while (!currentDate.isAfter(endDate)){
        val formattedStr = TimeFormat(currentDate.atTime(0, 0), format)
        dateList.add(StringObjPair(formattedStr, currentDate))
        currentDate = currentDate.plusDays(1)
    }
    return dateList
}

@Composable
fun TimeWheel(maxDayDiff : Int, date: LocalDate, onDismiss: () -> Unit, onConfirm: (LocalDateTime) -> Unit, currDay : LocalDate){
    val dates =  DateRangeStrPair(date.minusDays(maxDayDiff.toLong()), date.plusDays(maxDayDiff.toLong()))

    val hours = GetIntStrPair(0, 23)
    val minutes = GetIntStrPair(0, 59)

    var dateTimeSelected by remember { mutableStateOf(currDay.atTime(0, 0)) }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.4f),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column() {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    WheelPicker(
                        dates, onSelect = { date, index ->
                            dateTimeSelected.withDayOfYear(date.dayOfYear)
                        },
                        onPressed = { date, index ->
                            dateTimeSelected.withDayOfYear(date.dayOfYear)
                        },
                        itemSize = Size(200f, 40f),
                        initialObj = dateTimeSelected.toLocalDate()
                    )
                    WheelPicker(
                        hours, onSelect = { time, index ->
                            dateTimeSelected.withHour(time)
                        },
                        onPressed = { time, index ->
                            dateTimeSelected.withHour(time)
                        },
                        itemSize = Size(70f, 40f),
                        initialObj = dateTimeSelected.hour
                    )
                    WheelPicker(
                        minutes, onSelect = { time, index ->
                            dateTimeSelected.withMinute(time)
                        },
                        onPressed = { time, index ->
                            dateTimeSelected.withMinute(time)
                        },
                        itemSize = Size(70f, 40f),
                        initialObj = dateTimeSelected.minute
                    )
                }
                Spacer(Modifier.height(15.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    Button(onClick = {
                        onConfirm(dateTimeSelected)
                        onDismiss()
                    }) {
                        Text("Confirm")
                    }

                    Button(onClick = {
                        onDismiss()
                    }) {
                        Text("Cancel")
                    }

                }
            }
        }
    }
}

@Composable
fun EditEventDialog(event : Event? = null, date: LocalDate, onDismiss: ()->Unit,
                    onConfirm: (Event)->Unit){


    var routine by remember { mutableStateOf(event?.routine?:Routine()) }
    var name by remember { mutableStateOf(event?.name?:"") }
    var allDay by remember { mutableStateOf(event?.wholeDay?:false) }

    var displayStartTimeWheel by remember { mutableStateOf(false) }
    var displayEndTimeWheel by remember { mutableStateOf(false) }
    var displayRoutineSelector by remember { mutableStateOf(false) }

    var startTime by remember { mutableStateOf(event?.let{it.routine.startDate.atTime(it.startTime)}?: date.atTime(0,0)) }
    var endTime by remember { mutableStateOf(event?.let{it.routine.endDate?.atTime(it.endTime)}?:date.atTime(0,0)) }


    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.6f),
            shape = RoundedCornerShape(20.dp)
        ) {
            if (displayStartTimeWheel)
                TimeWheel(
                    365,
                    date,
                    { displayStartTimeWheel = false },
                    { startTime = it },
                    date)
            if (displayEndTimeWheel)
                TimeWheel(
                    365,
                    date,
                    { displayEndTimeWheel = false },
                    { endTime = it },
                    date)

            if (displayRoutineSelector)
                RoutineDialog(
                    { routine = it },
                    { displayRoutineSelector = false },
                    { routine = Routine() },
                    routine
                )

            Scaffold(
                bottomBar = {
                    Row(Modifier.fillMaxWidth()){
                        val confirmColor = if (name != "") Color.Unspecified else Color.Gray
                        Spacer(Modifier.weight(1f))
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = confirmColor),
                            onClick = {
                                if (name != "") {
                                    onDismiss()
                                    onConfirm(
                                        Event(
                                            name = name, wholeDay = allDay,
                                            startTime = startTime.toLocalTime(),
                                            endTime = endTime.toLocalTime(),
                                            routine = Routine(
                                                startDate = startTime.toLocalDate(),
                                                endDate = endTime.toLocalDate()
                                            )
                                        )
                                    )
                                }
                        }){
                            Text("Confirm")
                        }
                        Button(onClick = {
                            onDismiss()
                        },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                            Text("Cancel")
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    Modifier.fillMaxWidth()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Title") },
                        singleLine = true
                    )
                    Spacer(Modifier.height(20.dp))
                    Row(){
                        Text("All day")
                        Spacer(Modifier.weight(1f))
                        Switch(checked = allDay,
                            onCheckedChange = {allDay = it})
                    }
                    Spacer(Modifier.height(20.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { displayStartTimeWheel = true }) {
                            if (allDay)
                                Text(TimeFormat(startTime.withHour(0).withMinute(0), "EEE, MM/dd HH-mm"))
                            else
                                Text(TimeFormat(startTime, "EEE, MM/dd HH-mm"))
                        }
                        Spacer(Modifier.height(15.dp))

                        Text("—", color = Color.Black, style = MaterialTheme.typography.headlineMedium)

                        Spacer(Modifier.height(15.dp))
                        Button(onClick = { displayEndTimeWheel = true }) {
                            if (allDay)
                                Text(TimeFormat(startTime.withHour(0).withMinute(0), "EEE, MM/dd HH-mm"))
                            else if (endTime.isAfter(startTime))
                                Text(TimeFormat(endTime, "EEE, MM/dd HH-mm"))
                            else Text(TimeFormat(startTime, "EEE, MM/dd HH-mm"))
                        }
                    }
                    Spacer(Modifier.height(40.dp))
                    Button(onClick = {displayRoutineSelector = true}) {
                        Text("Repeat")
                    }
                }
            }
        }
    }
}

@Composable
fun EventDetailDialog(event : Event, onDismiss: ()->Unit){
    val startTime = event.routine.startDate.atTime(event.startTime)
    val endTime = event.routine.endDate?.atTime(event.endTime)
    val dateRange = DateRangeString(startTime, endTime, "MMM-dd-yyyy")
    val timeRange = DateRangeString(startTime, endTime, "HH:mm")


    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.6f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Scaffold(
                bottomBar = {
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(Modifier.weight(1f))
                        Button(onClick = { onDismiss() }) {
                            Text("Close")
                        }
                    }
                }
            ) { paddingValues ->

                Column(Modifier.padding(30.dp).padding(paddingValues), verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(15.dp)).background(Color.LightGray).padding(10.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .width(10.dp)
                                .height(15.dp)
                                .background(event.type.color)
                        ) {}
                        Spacer(Modifier.width(10.dp))
                        Text(
                            event.type.name,
                            color = event.type.color,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.weight(1f)
                        )

                    }
                    Box(Modifier.fillMaxWidth().padding(10.dp).clip(RoundedCornerShape(15.dp)).background(Color.LightGray)) {
                        Text(dateRange, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(10.dp))
                    }
                    Box(Modifier.fillMaxWidth().padding(10.dp).clip(RoundedCornerShape(15.dp)).background(Color.LightGray)) {
                        Text(timeRange, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ViewEventDialog(eventList: List<Event>, onDismiss: () -> Unit){
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    val sortedEvents = SortEvents(eventList)
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.6f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Scaffold(
                topBar = {
                    Row(Modifier.padding(30.dp)) {
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
                        Text("Events", color = Color.Black)
                        Spacer(Modifier.weight(1f))
                    }
                }
            ) { paddingValues ->
                LazyColumn(Modifier.padding(paddingValues)) {
                    sortedEvents.forEach { eventTypes ->
                        val type = eventTypes.first
                        val events = eventTypes.second
                        item(key = type.id) {
                            Row() {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .width(10.dp)
                                        .height(15.dp)
                                        .background(type.color)
                                ) {}
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    type.name,
                                    color = type.color,
                                    style = MaterialTheme.typography.headlineMedium,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                            }
                        }
                        items(items = events, key = { it.id }) { event ->
                            Button(
                                onClick = { selectedEvent = event },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(15.dp),
                            ) {
                                Text(event.name)
                            }
                        }
                    }
                }
            }
        }

    }
    selectedEvent?.let {
        EventDetailDialog(it, {selectedEvent = null})
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarPage(viewModel: EventViewModel, modifier: Modifier){



    var displayedMonth by remember {mutableStateOf(YearMonth.now())}

   // val datePickerState = rememberDatePickerState()



    var pickedDate by remember { mutableStateOf("") }
  //  datePickerState.selectedDateMillis?.let{ pickedDate = ConvertMilisToDate(it)}

    var selectViewPopup by remember { mutableStateOf(false) }


    var selectedDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }

    var showEventsScreen by remember { mutableStateOf(false) }
    var addEventsScreen by remember { mutableStateOf(false) }

    var monthDropdown by remember {mutableStateOf(false)}

    val monthOffset = 36

    val monthList = remember(displayedMonth){
        val months = mutableListOf<StringObjPair<YearMonth>>()
        var currMonth = displayedMonth.minusMonths(monthOffset.toLong())
        for (i in 1..monthOffset * 2){
            currMonth = currMonth.plusMonths(1)
            months.add(StringObjPair(currMonth.toString(), currMonth))
        }
        months

    }

   // var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Scaffold(
        topBar = {
            Row(Modifier.fillMaxWidth()){
                Button(onClick = {monthDropdown = !monthDropdown}){
                    Text(displayedMonth.toString(), style = MaterialTheme.typography.headlineMedium)

                }
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = { displayedMonth = displayedMonth.minusMonths(1) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                IconButton(
                    onClick = { displayedMonth = displayedMonth.plusMonths(1) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            }
        },
        bottomBar = {Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(1f))
            Button(onClick = {
                selectViewPopup = true
                //  datePickerState.selectedDateMillis?.let{selectedDate = ConvertMilisToLocalDateTime(it).toLocalDate()}
            }) {
                Text("Open")
            }
        }}
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            if (monthDropdown){

                Popup(properties = PopupProperties()) {
                    Surface(
                        color = Color.White
                    ) {
                        WheelPicker(
                            monthList,
                            { month: YearMonth, index: Int ->
                                displayedMonth = month

                            },
                            { month: YearMonth, index: Int ->
                                displayedMonth = month
                                monthDropdown = false
                            },
                            Size(144f, 64f),
                            initialObj = displayedMonth
                        )
                    }
                }
            }
            OutlinedTextField(
                value = pickedDate, readOnly = true, onValueChange = {})

            CalendarEventDisplay(displayedMonth, viewModel) { selectedDate = it
                pickedDate = TimeFormat(selectedDate.atTime(0,0), "yyyy-MM-dd")
            }
//        DatePicker(
//            state = datePickerState,
//            modifier = Modifier.fillMaxWidth(),
//            title = { Text("Select Date Range") })
            Spacer(Modifier.height(15.dp))
            HorizontalDivider(Modifier.fillMaxWidth())
            Spacer(Modifier.height(15.dp))



        }
    }
    if (selectViewPopup){
        Dialog(
            onDismissRequest = { selectViewPopup = false }
        ){
            Card(
                modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.6f),
                shape = RoundedCornerShape(20.dp)){
                Column(Modifier.fillMaxWidth()) {
                    Button(onClick = {showEventsScreen = true
                        selectViewPopup = false
                    }){
                        Text("Show Events")
                    }

                    Button(onClick = {addEventsScreen = true
                        selectViewPopup = false}){
                        Text("Add Events")
                    }
                }
            }
        }
    }

    if (showEventsScreen){
        ViewEventDialog(viewModel.GetEvents({it.OnDay(selectedDate)}),
            {showEventsScreen = false})
    }
    if (addEventsScreen){
        EditEventDialog(onDismiss = {addEventsScreen = false}, onConfirm = {viewModel.AddEvent(it)}, date = selectedDate)
    }
}