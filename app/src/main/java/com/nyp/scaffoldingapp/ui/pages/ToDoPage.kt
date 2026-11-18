package com.nyp.scaffoldingapp.ui.pages


import android.util.Log
import java.time.Instant
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.ChangeHistory
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

import com.nyp.scaffoldingapp.ui.KaiserClasses.CompletionProgress
import com.nyp.scaffoldingapp.ui.KaiserClasses.Edit
import com.nyp.scaffoldingapp.ui.KaiserClasses.RoutineType
import com.nyp.scaffoldingapp.ui.KaiserClasses.SubjectViewModel
import com.nyp.scaffoldingapp.ui.KaiserClasses.TimeFormat
import com.nyp.scaffoldingapp.ui.KaiserClasses.ToDoSubjects
import kotlinx.coroutines.flow.filter
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale
import java.util.Date
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.window.PopupProperties
import com.nyp.scaffoldingapp.ui.KaiserClasses.Routine

//@Composable



@Composable
fun TasksDisplay(subjects: ToDoSubjects, modifier: Modifier,
                 viewModel : SubjectViewModel,
                 onEditSubject: (Subject)->Unit,
                 onDeleteSubject: (Subject)->Unit,
                 onAddTask: (Subject)->Unit,
                 onEditTask: (Task)-> Unit,
                 onDeleteTask: (Task)->Unit,
                 taskInterjection: @Composable (Subject) ->Unit
                 ){
    var selectedSubjectDropdown by remember { mutableStateOf<Subject?>(null) }
    var progressDropdownTask by remember { mutableStateOf<Task?>(null)}
    LazyColumn(modifier) {

        subjects.subjects.forEach { subject ->
            item(key = subject.id) {
                DisplaySubject(
                    subject, onEditSubject = onEditSubject,
                    onDeleteSubject = onDeleteSubject,
                    onSelect = { selectedSubjectDropdown = it },
                    onAddTask = onAddTask,
                    selectedSubjectDropdown,
                    { selectedSubjectDropdown = null })
            }

            items(subject.tasks, key = {it.id}) { task ->
               if (task.routine.OnDay(LocalDate.now())) {
                    Row(
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(color = Color.LightGray),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.width(7.dp))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.LightGray)
                                .clickable {
                                    progressDropdownTask = task
                                }
                        ) {
                            when (task.completionProgress) {
                                (CompletionProgress.NOT_DONE)-> {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Not Done"
                                    )
                                }

                                (CompletionProgress.PARTIALLY_DONE)-> {
                                    Icon(
                                        imageVector = Icons.Outlined.ChangeHistory,
                                        contentDescription = "Half Done"
                                    )
                                }

                                (CompletionProgress.DONE)-> {
                                    Icon(
                                        imageVector = Icons.Outlined.Circle,
                                        contentDescription = "Done"
                                    )
                                }
                            }
                        }
                        DropdownMenu(
                            expanded = progressDropdownTask == task,
                            onDismissRequest = { progressDropdownTask = null }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Not Done"
                                    )
                                    Spacer (Modifier.width(30.dp))
                                    Text("Not Done", modifier = Modifier.offset(x = 30.dp))
                                },
                                onClick = {
                                    viewModel.EditTask(
                                        task, progress = Edit.Change(
                                            CompletionProgress.NOT_DONE
                                        )
                                    )

                                    progressDropdownTask = null

                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Icon(
                                        imageVector = Icons.Outlined.ChangeHistory,
                                        contentDescription = "Half Done"
                                    )
                                    Spacer (Modifier.width(30.dp))
                                    Text("Half Done", modifier = Modifier.offset(x = 30.dp))
                                },
                                onClick = {
                                    viewModel.EditTask(
                                        task, progress = Edit.Change(
                                            CompletionProgress.PARTIALLY_DONE
                                        )
                                    )
                                    progressDropdownTask = null

                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Icon(
                                        imageVector = Icons.Outlined.Circle,
                                        contentDescription = "Done"
                                    )
                                    Text("Done", modifier = Modifier.offset(x = 30.dp))
                                },
                                onClick = {
                                    viewModel.EditTask(
                                        task, progress = Edit.Change(
                                            CompletionProgress.DONE
                                        )
                                    )
                                    progressDropdownTask = null
                                }
                            )
                        }





                        Spacer(Modifier.width(10.dp))
                        DisplayTask(
                            task,
                            Color.Black,
                            onEditTask = onEditTask,
                            onDeleteTask = onDeleteTask
                        )
                    }
                }

            }
            item() {
                taskInterjection(subject)
            }

        }
    }



}

@Composable
fun DisplaySubject(
    subject: Subject,
    onEditSubject: (Subject)->Unit,
    onDeleteSubject: (Subject)->Unit,
    onSelect: (Subject)->Unit,
    onAddTask: (Subject)->Unit,
    dropDownSubj: Subject?,
    dropdownDismiss: ()->Unit
){
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .height(35.dp)
        .background(Color(0xffa3a3a3))
        .fillMaxWidth()) {
        Spacer(Modifier.width(5.dp))
        Box(modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .width(10.dp)
            .height(15.dp)
            .background(subject.color)){}
        Spacer(Modifier.width(10.dp))
        Text(subject.name, color = subject.color, style = MaterialTheme.typography.headlineMedium, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(30.dp))
        Button(onClick = {onAddTask(subject)}, modifier = Modifier
            .size(20.dp),shape = RoundedCornerShape(3.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),border = BorderStroke(color = Color.White, width = 2.dp), contentPadding = PaddingValues(0.dp)){Text("+")}
        Spacer(Modifier.width(2.dp))
        Row() {
            Button(
                onClick = {onSelect(subject)},
                elevation = null,
                modifier = Modifier
                    .width(15.dp)
                    .height(20.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            ) { Text(":") }
            Spacer(Modifier.width(2.dp))
            DropdownMenu(
                expanded = dropDownSubj == subject,
                onDismissRequest = dropdownDismiss
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = { onEditSubject(subject) }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = { onDeleteSubject(subject) }
                )
                DropdownMenuItem(
                    text = { Text("Close") },
                    onClick = { dropdownDismiss() }
                )
            }
        }

    }
}


@Composable
fun DisplayTask(task : Task,
                color: Color,
                modifier: Modifier = Modifier,
                onEditTask: (Task)->Unit,
                onDeleteTask: (Task)->Unit){

    var menuTask : Task? by remember { mutableStateOf(null) }
    Row(modifier){

        Text(task.name, color = color, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        Spacer(Modifier.weight(1f))
        //Text(DateRangeString(task.time?.se), color = color, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        Row() {
            task.time?.let{(DateTimeFormat(it))}
            Spacer(Modifier.width(2.dp))
            Button(
                onClick = {menuTask = task},
                elevation = null,
                modifier = Modifier
                    .width(15.dp)
                    .height(20.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            ) {
                Text(":", color = Color.Black)
                DropdownMenu(
                    expanded = menuTask == task,
                    onDismissRequest = { menuTask = null }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = { onEditTask(task) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = { onDeleteTask(task) }
                    )
                    DropdownMenuItem(
                        text = { Text("Close") },
                        onClick = { menuTask = null }
                    )
                }
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}

fun ConvertMilisToDate(millis : Long) : String{
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
//
@OptIn(ExperimentalTime::class)
fun ConvertMilisToLocalDateTime(millis : Long) : LocalDateTime{
    val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
    return localDateTime
}

data class StringObjPair<T>(
    var name : String,
    var obj : T
)

@Composable
fun <T>WheelPicker(
    items: List<StringObjPair<T>>,
    onSelect: (T, Int)->Unit,
    onPressed: (T, Int)->Unit,
    itemSize: Size,
    visibleItems : Int = 5,
    initialIndex : Int = 0,
    textSizeMult : Float = 1.5f,
    modifier: Modifier = Modifier
){
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFling = rememberSnapFlingBehavior(lazyListState = listState)
    val height = itemSize.height * visibleItems




    val centerIndex by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val center = (info.viewportStartOffset + info.viewportEndOffset) / 2

            info.visibleItemsInfo
                .minByOrNull { abs(it.offset + it.size / 2 - center) }
                ?.index ?: initialIndex
        }
    }
    onSelect(items[centerIndex].obj, centerIndex)

    LaunchedEffect(listState) {
        snapshotFlow{ listState.isScrollInProgress }
            //filter it just means it calls when snapshotFlow is true
            .filter { !it }
            .collect{ onSelect(items[centerIndex].obj, centerIndex)}
    }

    Box(modifier.width(itemSize.width.dp).height(height.dp), contentAlignment = Alignment.Center){
        Box(Modifier.fillMaxWidth().height(itemSize.height.dp).clip(RoundedCornerShape(12.dp)))
        var columnCenter = 0
        LazyColumn(            state = listState,
            flingBehavior = snapFling,
            contentPadding = PaddingValues(vertical = (height * 0.5f - itemSize.height * 0.5f).dp),
            modifier = Modifier.fillMaxSize()
                .onGloballyPositioned{columnCenter = it.size.height / 2}) {
            itemsIndexed(items) { index, item ->
                var distance by remember { mutableStateOf(1f) }
                var cosCurve by remember { mutableStateOf(0.0)}
                Button(
                    onClick = {onPressed(item.obj, index)},
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemSize.height.dp)
                        .onGloballyPositioned{
                            var btnCenter = it.positionInParent().y
                            btnCenter += it.size.height / 2
                            distance = abs(columnCenter - btnCenter)
                            cosCurve = abs(cos((distance) / (itemSize.height * visibleItems) * 0.25f * (2 * PI) + PI)).coerceAtLeast(0.25)
                        }
                        .alpha((cosCurve.toFloat()))
                ) {
                    Text(
                        text = item.name,
                        modifier = Modifier.graphicsLayer(scaleY = cosCurve.toFloat() * textSizeMult, scaleX = textSizeMult)
                    )


                }
            }
        }

    }
}





@Composable
fun <T>WheelPicker(
    items: List<StringObjPair<T>>,
    onSelect: (T, Int)->Unit,
    onPressed: (T, Int)->Unit,
    itemSize: Size,
    visibleItems : Int = 5,
    initialObj : T,
    textSizeMult : Float = 1.5f,
    modifier: Modifier = Modifier
){
    var initialIndex = 0
    items.forEachIndexed {index, item ->
        if (initialObj == item.obj)
            initialIndex = index
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFling = rememberSnapFlingBehavior(lazyListState = listState)
    val height = itemSize.height * visibleItems




    val centerIndex by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val center = (info.viewportStartOffset + info.viewportEndOffset) / 2

            info.visibleItemsInfo
                .minByOrNull { abs(it.offset + it.size / 2 - center) }
                ?.index ?: initialIndex
        }
    }
    onSelect(items[centerIndex].obj, centerIndex)

    LaunchedEffect(listState) {
        snapshotFlow{ listState.isScrollInProgress }
            //filter it just means it calls when snapshotFlow is true
            .filter { !it }
            .collect{ onSelect(items[centerIndex].obj, centerIndex)}
    }

    Box(modifier.width(itemSize.width.dp).height(height.dp), contentAlignment = Alignment.Center){
        Box(Modifier.fillMaxWidth().height(itemSize.height.dp).clip(RoundedCornerShape(12.dp)))
        var columnCenter = 0
        LazyColumn(            state = listState,
            flingBehavior = snapFling,
            contentPadding = PaddingValues(vertical = (height * 0.5f - itemSize.height * 0.5f).dp),
            modifier = Modifier.fillMaxSize()
                .onGloballyPositioned{columnCenter = it.size.height / 2}) {
            itemsIndexed(items) { index, item ->
                var distance by remember { mutableStateOf(1f) }
                var cosCurve by remember { mutableStateOf(0.0)}
                Button(
                    onClick = {onPressed(item.obj, index)},
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemSize.height.dp)
                        .onGloballyPositioned{
                            var btnCenter = it.positionInParent().y
                            btnCenter += it.size.height / 2
                            distance = abs(columnCenter - btnCenter)
                            cosCurve = abs(cos((distance) / (itemSize.height * visibleItems) * 0.25f * (2 * PI) + PI)).coerceAtLeast(0.25)
                        }
                        .alpha((cosCurve.toFloat()))
                ) {
                    Text(
                        text = item.name,
                        modifier = Modifier.graphicsLayer(scaleY = cosCurve.toFloat() * textSizeMult, scaleX = textSizeMult)
                    )


                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(): TimePickerState{
    val currentTime = LocalTime.now()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = true
    )
    Column(modifier = Modifier.fillMaxWidth()){
        TimeInput(
            state = timePickerState
        )
    }
    return timePickerState
}


@Composable
fun DatePickerDocked(onSelect : (LocalDateTime)-> Unit){
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let{ ConvertMilisToDate(it)} ?: ""
    Box(modifier = Modifier.fillMaxWidth()){
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker}){
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "DatePicker"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(64.dp)
        )
    }
    if (showDatePicker){
        Popup(onDismissRequest = {showDatePicker = false}, alignment = Alignment.TopStart, properties = PopupProperties(focusable = true)){
            Box(modifier = Modifier.fillMaxWidth().offset(y = 64.dp).background(MaterialTheme.colorScheme.surface).padding(16.dp)){

                DatePicker(state = datePickerState, showModeToggle = false)
                val selectedDate: Long? = datePickerState.selectedDateMillis
                if (selectedDate != null) {
                    onSelect(ConvertMilisToLocalDateTime(selectedDate))
                }
            }
        }
    }
}

fun DateRangeString(startTime : LocalDateTime?, endTime : LocalDateTime?, format : String?) : String{
    var timeVal = "Date Range"

    val formatter = { time: LocalDateTime -> format?.let{TimeFormat(time, format)}?:TimeFormat(time)}

    if (startTime != null && endTime != null) {
        timeVal = "${formatter(
            startTime)} - ${formatter(
            endTime)}"

    }
    else if (startTime != null) {
        timeVal = formatter(
            startTime)
    }
    return timeVal
}

fun DateRangeString(startTime : Long?, endTime : Long?, format : String?) : String{
    var timeVal = "Date Range"

    val formatter = { time: LocalDateTime -> format?.let{TimeFormat(time, format)}?:TimeFormat(time)}

    if (startTime != null && endTime != null) {
        timeVal = "${formatter(
            ConvertMilisToLocalDateTime(startTime))} - ${formatter(
            ConvertMilisToLocalDateTime(endTime))}"

    }
    else if (startTime != null) {
        timeVal = formatter(
            ConvertMilisToLocalDateTime(startTime))
    }
    return timeVal
}




@Composable
fun DateRangePickerDocked(onSelect : (LocalDate, LocalDate)-> Unit,
                          onDismiss: () -> Unit){
    val datePickerState = rememberDateRangePickerState()

    val selectedStartDate = datePickerState.selectedStartDateMillis
    val selectedEndDate = datePickerState.selectedEndDateMillis

    DatePickerDialog(onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { if (selectedStartDate != null && selectedEndDate != null)
                onSelect(ConvertMilisToLocalDateTime(selectedStartDate).toLocalDate(), ConvertMilisToLocalDateTime(selectedEndDate).toLocalDate())
                onDismiss()
            }){
                Text("Confirm")
            }

        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }){

        val timeVal = DateRangeString(selectedStartDate, selectedEndDate, "yyyy-MM-dd")
        OutlinedTextField(
            value = timeVal, readOnly = true, onValueChange = {})

        DateRangePicker(state = datePickerState, modifier = Modifier.fillMaxWidth(), title = {Text("Select Date Range")})
    }
}


@Composable
fun DeleteTaskDialog(task: Task,
                 onDismiss: ()->Unit,
                     viewModel: SubjectViewModel){
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.6f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column() {
                Spacer(Modifier.weight(1f))
                Text("Do you want to delete ${task.name}?", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(15.dp))
                Row(modifier = Modifier.fillMaxWidth()){
                    Spacer(Modifier.weight(1f))
                    Button(onClick = {
                        viewModel.DeleteTask(task)
                        onDismiss()
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)){
                        Text("Delete", style = MaterialTheme.typography.headlineSmall)
                    }
                    Spacer(Modifier.weight(1f))
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black)){
                        Text("Cancel", style = MaterialTheme.typography.headlineSmall)
                    }
                    Spacer(Modifier.weight(1f))

                }
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

fun ConvertDurationToString(duration: Duration) : String{
    val hours = duration.inWholeHours
    val minutes = duration.inWholeMinutes
    return "${hours}Hour ${minutes % 60}m"
}

fun GetDurationInRange(maxDuration: Duration, intervalMin: Int) : MutableList<Duration>{
    val durationList : MutableList<Duration> = mutableListOf()
    val numVals : Int = (maxDuration.inWholeMinutes / intervalMin).toInt()
    for (i in 1..numVals){
        durationList.add((intervalMin * i).minutes)
    }
    return durationList
}

fun GetDurationListStrPair(list: List<Duration>) : MutableList<StringObjPair<Duration>>{
    val durList : MutableList<StringObjPair<Duration>> = mutableListOf()
    list.forEach{ duration ->
        durList.add(StringObjPair(ConvertDurationToString(duration), duration))
    }
    return durList
}

fun GetIntStrPair(minInt : Int, maxInt : Int, interval : Int = 1) : MutableList<StringObjPair<Int>>{
    val intList = mutableListOf<StringObjPair<Int>>()
    for (i in minInt..maxInt){
        intList.add(StringObjPair(i.toString(), i))
    }
    return intList
}

@Composable
fun RoutineDialog(
    onClick : (Routine)->Unit,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    routineOG: Routine
){
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(20.dp)
        ) {

            var selectedStartDate by remember { mutableStateOf<LocalDate?>(routineOG.startDate) }
            var selectedEndDate by remember { mutableStateOf<LocalDate?>(routineOG.startDate) }

            var datePickerOpen by remember { mutableStateOf(false) }
            var intervalPickerOpen by remember { mutableStateOf(false) }

            var frequency by remember { mutableIntStateOf(routineOG.frequency) }
            var freqIndex by remember {mutableIntStateOf(0)}
            var routineType by remember { mutableStateOf(routineOG.routineType) }
            var routineIndex by remember {mutableIntStateOf(1)}

            val daysRepeat = remember { mutableStateListOf<DayOfWeek>() }

            val routine = Routine(routineType = RoutineType.WEEKLY)



            Scaffold(topBar = {
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
                    Text("Routine", color = Color.Black)
                    Spacer(Modifier.weight(1f))
                    Button(onClick = { onClick(routine)
                    onDismiss()}) {
                        Text("Done", color = Color.Black)
                    }
                }
            }) { padding->
                Column(Modifier.padding(padding)) {


                    Button(
                        onClick = { intervalPickerOpen = !intervalPickerOpen },
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text("$frequency ${if (routineType == RoutineType.WEEKLY) "Week" else "Day"}")
                    }
                    if (intervalPickerOpen){
                        if (routineType == RoutineType.NONE)
                            routineType = RoutineType.WEEKLY
                        Dialog(
                            onDismissRequest = {intervalPickerOpen = false},
                            properties = DialogProperties(usePlatformDefaultWidth = false)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .fillMaxHeight(0.5f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Scaffold(
                                    topBar = {
                                        val repeatIntervalStr = "Repeat interval - Every $frequency ${RoutineType.WEEKLY.toString()}"
                                        Row(horizontalArrangement = Arrangement.Center) {
                                            Text(repeatIntervalStr)
                                        }
                                    },
                                    bottomBar = {
                                        Row(horizontalArrangement = Arrangement.Absolute.Right) {
                                            Button(onClick = {
                                                onClick(routineOG)
                                                intervalPickerOpen = false
                                            }){
                                                Text("Cancel")
                                            }
                                            Spacer(Modifier.padding(15.dp))
                                            Button(onClick = {
                                                routine.routineType = routineType
                                                routine.frequency = frequency
                                                routine.startDate = selectedStartDate?:routineOG.startDate
                                                routine.endDate = selectedEndDate
                                                routine.days = daysRepeat

                                                onClick(routineOG)
                                                intervalPickerOpen = false
                                            }){
                                                Text("Confirm")
                                            }
                                        }
                                    }
                                ) { padding ->
                                    Row(Modifier.padding(padding)) {
                                        WheelPicker(
                                            GetIntStrPair(1, 100),
                                            onPressed = { freq: Int, index: Int ->
                                                frequency = freq
                                                freqIndex = index
                                            },
                                            onSelect = { freq: Int, index: Int ->
                                                frequency = freq
                                                freqIndex = index
                                            },
                                            initialIndex = freqIndex,
                                            itemSize = Size(80f, 60f)
                                        )

                                        WheelPicker(
                                            listOf(
                                                StringObjPair("Day", RoutineType.DAILY),
                                                StringObjPair("Week", RoutineType.WEEKLY)
                                            ),
                                            onPressed = { routine: RoutineType, index: Int ->
                                                routineType = routine
                                                routineIndex = index
                                            },
                                            onSelect = { routine: RoutineType, index: Int ->
                                                routineType = routine
                                                routineIndex = index
                                            },
                                            initialIndex = routineIndex,
                                            itemSize = Size(80f, 60f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { datePickerOpen = !datePickerOpen },
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(DateRangeString(selectedStartDate?.atStartOfDay(), selectedEndDate?.atStartOfDay(), "yyyy-MM-dd"))
                    }

                    if (datePickerOpen)
                        DateRangePickerDocked(
                            onSelect = { startDate: LocalDate, endDate: LocalDate ->
                                selectedEndDate = endDate
                                selectedStartDate = startDate
                            },
                            onDismiss = { datePickerOpen = false }
                        )
                    Spacer(Modifier.height(5.dp))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(5.dp))

                    if (routine.routineType == RoutineType.WEEKLY) {
                        for (day in DayOfWeek.entries) {
                            Button(
                                onClick = {
                                    if (day in daysRepeat) daysRepeat.remove(day)
                                    else daysRepeat.add(day)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(day.name)
                                Spacer(Modifier.weight(1f))
                                if (day in daysRepeat) {


                                    Text("✔")
                                }
                            }
                        }
                        Spacer(Modifier.height(5.dp))
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(5.dp))

                        Button(onClick = {
                            onReset()
                            onDismiss()
                        }) {
                            Text("No repeat")
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTask(subject: Subject,
             onDismiss: (Task, Subject)->Unit,
             viewModel: SubjectViewModel,
             modifier: Modifier = Modifier,
             task : Task,
             finished : Boolean
             ) {

    var showSubjDropDown by remember { mutableStateOf(false) }
    var showTimeDropDown by remember { mutableStateOf(false) }
    var showDurationDropDown by remember { mutableStateOf(false) }
    var showRoutineDropDown by remember { mutableStateOf(false) }


    val durationSaver = Saver<Duration?, Long>(
        save = {it?.inWholeMilliseconds},
        restore = {it.milliseconds}

    )

    var timeInterval by remember { mutableStateOf(task.routine.frequency) }
    var selectedSubj by remember { mutableStateOf(subject) }
    var selectedTime by remember {mutableStateOf<LocalDateTime?>(task.time)}

    val timePickerState = rememberTimePickerState()

    var selectedDuration by rememberSaveable(stateSaver = durationSaver) {mutableStateOf<Duration?>(task.duration)}
    var taskName by remember { mutableStateOf("") }

    var selectedRoutine by remember { mutableStateOf<Routine>(Routine())}

    val subjList by viewModel.subjects.collectAsStateWithLifecycle()


    val routine by remember{ mutableStateOf(Routine())}

    var durationSelectedIndex by rememberSaveable { mutableStateOf(0)}

    Row(modifier.fillMaxWidth()) {
        Column() {


        DropdownMenu(
            expanded = showSubjDropDown,
            onDismissRequest = { showSubjDropDown = false },
            Modifier.background(color = Color.Gray)
        ) {
            for (i in 1..subjList.subjects.size) {
                DropdownMenuItem(
                    onClick = { selectedSubj = subjList.subjects[i - 1] },
                    text = {
                        Row() {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .width(10.dp)
                                    .height(15.dp)
                                    .background(subject.color)
                            ) {}
                            Spacer(Modifier.width(10.dp))
                            Text(
                                subject.name,
                                color = subject.color,
                                style = MaterialTheme.typography.headlineSmall,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                        }
                    })
                }
            }
        }

        Button(
            onClick = { showSubjDropDown = !showSubjDropDown },
            Modifier.weight(1f)
        ) {
            Row() {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .width(10.dp)
                        .height(15.dp)
                        .background(subject.color)
                ) {}
                Spacer(Modifier.width(10.dp))
                Text(
                    subject.name,
                    color = subject.color,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

            }
        }
        Box(
            Modifier.weight(1f)) {
            DropdownMenu(
                expanded = showTimeDropDown,
                onDismissRequest = { showTimeDropDown = false }) {
                Column(){
                    Spacer(Modifier.height(30.dp))
                    DatePickerDocked({selectedTime = it})
                    TimePicker(
                        state = timePickerState
                    )

                }
            }

            Button(
                onClick = { showTimeDropDown = !showTimeDropDown }
            ) {
                Row() {
                    Spacer(Modifier.width(10.dp))
                    Text(
                        selectedTime?.let { TimeFormat(it.withHour(timePickerState.hour).withMinute(timePickerState.minute)) } ?: "Start Time",
                        color = subject.color,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                }
            }
        }

        Box(
            Modifier.weight(1f)) {
            DropdownMenu(expanded = showDurationDropDown,
                onDismissRequest = {showDurationDropDown = false}) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(Modifier.fillMaxWidth()) {
                        Button(onClick = { timeInterval = 1 }, modifier = Modifier.weight(1f)) {
                            Text("1 min")
                        }
                        Button(onClick = { timeInterval = 5 }, modifier = Modifier.weight(1f)) {
                            Text("5 min")
                        }
                    }
                    Row(Modifier.fillMaxWidth()) {
                        Button(onClick = { timeInterval = 10 }, modifier = Modifier.weight(1f)) {
                            Text("10 min")
                        }
                        Button(onClick = { timeInterval = 15 }, modifier = Modifier.weight(1f)) {
                            Text("15 min")
                        }

                    }
                    HorizontalDivider(Modifier.fillMaxWidth())

                    WheelPicker(
                        GetDurationListStrPair(GetDurationInRange(5.hours, timeInterval)),
                        onSelect = { duration: Duration, index: Int ->
                            durationSelectedIndex = index
                        },
                        onPressed = { duration: Duration, index: Int ->
                            selectedDuration = duration
                            showDurationDropDown = false
                            durationSelectedIndex = index
                        },
                        itemSize = Size(144f, 60f),
                        initialIndex = durationSelectedIndex
                    )
                    HorizontalDivider(Modifier.fillMaxWidth())
                    Button(onClick = {selectedDuration = null}){
                        Text("Unset Duration",Modifier.fillMaxWidth())
                    }
                }

            }

            Button(
                onClick = { showDurationDropDown = !showDurationDropDown }
            ) {
                Row() {
                    Spacer(Modifier.width(10.dp))
                    Text(
                        selectedDuration?.let { ConvertDurationToString(it) } ?: "Duration",
                        color = subject.color,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                }
            }
        }
        Box(
            Modifier.weight(1f)) {
//            DropdownMenu(
//                expanded = showRoutineDropDown,
//                onDismissRequest = { showRoutineDropDown = false }) {
//                Column(){
//                    Spacer(Modifier.height(30.dp))
//                    DatePickerDocked({selectedTime = it})
//                    TimePicker(
//                        state = timePickerState
//                    )
//


  //      }
            if (showRoutineDropDown){
                RoutineDialog(onClick = {selectedRoutine = it},
                    onDismiss = {showRoutineDropDown = false},
                    onReset = {selectedRoutine = Routine()},
                    selectedRoutine)
            }



            Button(
                onClick = { showRoutineDropDown = !showRoutineDropDown }
            ) {
                Row() {
                    Spacer(Modifier.width(10.dp))
                    Text(
                        selectedTime?.let { TimeFormat(it.withHour(timePickerState.hour).withMinute(timePickerState.minute)) } ?: "Start Time",
                        color = subject.color,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                }
            }
        }
    }

    LaunchedEffect(finished) {
        if (finished) {
            val newTask = GetNewTask()
            newTask.time = selectedTime
            newTask.duration = selectedDuration
            newTask.routine = selectedRoutine
            newTask.name = task.name
            onDismiss(newTask, selectedSubj)
        }
    }

}


@Composable
fun DeleteSubjDialog(subject: Subject,
                     onDismiss: ()->Unit,
                     viewModel: SubjectViewModel){
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.6f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column() {
                Spacer(Modifier.weight(1f))
                Text("Do you want to delete ${subject.name}?", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(15.dp))
                Row(modifier = Modifier.fillMaxWidth()){
                    Spacer(Modifier.weight(1f))
                    Button(onClick = {
                        viewModel.DeleteSubject(subject)
                        onDismiss()
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)){
                        Text("Delete", style = MaterialTheme.typography.headlineSmall)
                    }
                    Spacer(Modifier.weight(1f))
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black)){
                        Text("Cancel", style = MaterialTheme.typography.headlineSmall)
                    }
                    Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun SelectColor(colors : List<Color>,
                shape : Shape = CircleShape) : Color?{
    var selectedColor : Color? by remember { mutableStateOf(null) }

    Row() {
        for (i in 1..colors.size) {
            Spacer(Modifier.weight(1f))
            Button(shape = shape, colors = ButtonDefaults.buttonColors(containerColor = colors[i - 1]), onClick = {selectedColor = colors[i - 1]}) { }
        }
        Spacer(Modifier.weight(1f))
    }

    return selectedColor
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: ()->Unit,
    subjectViewModel: SubjectViewModel) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                var name by remember { mutableStateOf( task.name) }
                var routine by rememberSaveable { mutableStateOf(task.routine) }
                var dateTime by rememberSaveable { mutableStateOf(task.time)}
                var duration by rememberSaveable { mutableStateOf(task.duration) }
                var progress by rememberSaveable { mutableStateOf(task.completionProgress) }


                val timePickerState = TimePicker()

                Spacer(Modifier.height(15.dp))
                Row(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        onClick = {

                            subjectViewModel.EditTask(
                                task,
                                Edit.Change(name),
                                routine = Edit.Change(routine),
                                time = Edit.Change(dateTime),
                                duration = Edit.Change(duration),
                                progress = Edit.Change(progress)
                            )
                            onDismiss()

                        }) { Text("Confirm", color = Color.Black) }
                    Spacer(Modifier.weight(1f))
                    Button(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.weight(1f))

                }
            }
        }
    }
}


@Composable
fun SubjectDialog(
    subject: Subject? = null,
    onDismiss: () -> Unit,
    subjectViewModel: SubjectViewModel
    ) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                var name by remember { mutableStateOf(if (subject != null) subject.name else "Subject Name") }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name") },
                    singleLine = true
                )

                Spacer(Modifier.height(15.dp))
                Text("Select Color")
                Spacer(Modifier.height(15.dp))
                var subjColor: Color? by remember { mutableStateOf(if (subject != null) subject.color else Color.Black) }
                subjColor = SelectColor(
                    listOf(
                        Color.White,
                        Color.Green,
                        Color.Red,
                        Color.Blue,
                        Color.Yellow
                    )
                )

                val selectedColor: Color? = subjColor
                val buttonColor: Color
                if (selectedColor != null)
                    buttonColor = selectedColor
                else if (subject != null)
                    buttonColor = subject.color
                else buttonColor = Color.White
                Spacer(Modifier.height(15.dp))
                Row(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                        onClick = {

                            if (subject != null)
                                subjectViewModel.EditSubject(subject, selectedColor, name)
                            else {
                                subjectViewModel.AddSubject(
                                    name,
                                    if (selectedColor != null) selectedColor else Color.White
                                )
                            }
                            onDismiss()

                        }) { Text("Confirm", color = Color.Black) }
                    Spacer(Modifier.weight(1f))
                    Button(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.weight(1f))
                }

            }
        }
    }
}

@Composable
fun AddTaskRow(onFinished: (Task)->Unit, task : Task?){

    val newTask = task?:Task("Task Name", routine = Routine())
    var newName by rememberSaveable { mutableStateOf(newTask.name) }
    Row(Modifier.fillMaxWidth()){
        OutlinedTextField(
            value = newName,
            readOnly = false,
            label = { Text("Task Name") },
            singleLine = true,
            onValueChange = {newName = it}
        )
        IconButton(onClick = { onFinished(newTask)}){
            Icon(
                imageVector = Icons.Outlined.Circle,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
    }
}

fun GetNewTask() : Task{
    return Task("Task Name", routine = Routine())
}

@Composable
fun ToDoPage(viewModel: SubjectViewModel,modifier: Modifier) {


    var addSubjScreen by remember { mutableStateOf(false)}

    var addTaskScreen by remember { mutableStateOf<Subject?>(null) }
    var editTaskScreen by remember { mutableStateOf<Task?>(null) }
    var editSubjectScreen by remember { mutableStateOf<Subject?>(null) }
    var deleteTaskScreen by remember { mutableStateOf<Task?>(null) }
    var deleteSubjScreen by remember {mutableStateOf<Subject?>(null) }

    val state by viewModel.subjects.collectAsStateWithLifecycle()

    var dateTime by remember { mutableStateOf(LocalDateTime.now()) }

    var currDay by remember {mutableStateOf(LocalDate.now())}

    var newTask by remember { mutableStateOf(GetNewTask())}

    var finishedEditTask by remember { mutableStateOf(false)}

    //basically a coroutine
    LaunchedEffect(Unit) {
        while (true) {
            dateTime = LocalDateTime.now()
            if (LocalDate.now() != currDay){
                viewModel.SetTaskFinished(requirement = {task: Task ->
                    task.routine.OnDay(LocalDate.now())
                },
                    finished = CompletionProgress.NOT_DONE)

            }

            delay(1000L.milliseconds)
        }
    }
    Box(){
        Column(Modifier.fillMaxWidth().fillMaxHeight()) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
                    .border(color = Color(0xFF645f6b), width = 2.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(5.dp))

                val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                Text(
                    dateTime.format(dateFormatter),
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    dateTime.format(timeFormatter),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(Modifier.height(5.dp))
            Row() {
                Spacer(Modifier.width(15.dp))
                Button(
                    onClick = { addSubjScreen = true },
                    modifier = Modifier
                        .size(30.dp),
                    shape = RoundedCornerShape(3.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(color = Color.Black, width = 2.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text(text = "+", fontSize = 24.sp) }
                Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(5.dp))

            TasksDisplay(subjects = state, modifier,
                viewModel,
                onEditTask = {task: Task -> editTaskScreen = task},
                onAddTask = {subj : Subject -> addTaskScreen = subj
                newTask = GetNewTask()},
                onEditSubject = { subj: Subject -> editSubjectScreen = subj },
                onDeleteSubject = { subj: Subject -> deleteSubjScreen = subj},
                onDeleteTask = { task : Task -> deleteTaskScreen = task},
                taskInterjection = {subj : Subject ->
                    Row(Modifier.fillMaxWidth()) {
                        if (subj.id == addTaskScreen?.id) {
                            AddTaskRow(onFinished = {
                                newTask.name = it.name
                                finishedEditTask = true
                            }, newTask)

                        }

                    }
                })
        }
        if (addSubjScreen) {
            SubjectDialog(
                onDismiss = { addSubjScreen = false },
                subjectViewModel = viewModel
            )
        }
        editSubjectScreen?.let{SubjectDialog(
            onDismiss = { editSubjectScreen = null },
            subjectViewModel = viewModel,
            subject = it
        )}
        addTaskScreen?.let{ subj -> EditTask(onDismiss = { task: Task, subject: Subject ->
            viewModel.AddTask(task = task, subject = subject)
            addTaskScreen = null

            finishedEditTask = false

        },

            modifier = Modifier.height(60.dp).align(Alignment.BottomCenter),
            subject = subj,
            viewModel = viewModel,
            task = newTask,
            finished = finishedEditTask
            )

        }
        deleteTaskScreen?.let{DeleteTaskDialog(task = it,
            onDismiss = { deleteTaskScreen = null},
            viewModel)}
        editTaskScreen?.let{
            EditTaskDialog(task = it,
                onDismiss = { editTaskScreen = null },
                viewModel)
        }
        deleteSubjScreen?.let{
            DeleteSubjDialog(it,
                onDismiss = {deleteSubjScreen = null },
                viewModel)
        }
    }


}






//@Preview(showBackground = true)
//@Composable
//private fun ToDoPagePreview(){
//  //  val viewModel = ToDoPage(Modifier)
////    MaterialTheme{
////        ToDoPage(Modifier.background(Color.White))
////    }
//}