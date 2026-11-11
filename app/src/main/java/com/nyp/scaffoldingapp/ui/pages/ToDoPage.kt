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

//@Composable



@Composable
fun TasksDisplay(subjects: ToDoSubjects, modifier: Modifier,
                 viewModel : SubjectViewModel,
                 onEditSubject: (Subject)->Unit,
                 onDeleteSubject: (Subject)->Unit,
                 onAddTask: (Subject)->Unit,
                 onEditTask: (Task)-> Unit,
                 onDeleteTask: (Task)->Unit){
    var menuSubject by remember { mutableStateOf<Subject?>(null) }

    Column(modifier) {
        for (f in 1..subjects.subjects.size){

            val subject = subjects.subjects[f-1]
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
                        onClick = { menuSubject = subject },
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
                        expanded = menuSubject == subject,
                        onDismissRequest = { menuSubject = null }
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
                            onClick = { menuSubject = null }
                        )
                    }
                }

            }
            for (i in 1..subject.tasks.size) {
                Row(modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(color = Color.LightGray), verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(7.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(24.dp)
                            .border(
                                width = 2.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .background(Color.LightGray)
                            .clickable {
                                viewModel.SetTaskFinished(
                                    subject.tasks[i - 1],
                                    !subject.tasks[i - 1].finished
                                )
                            }
                    ) {
                        if (subject.tasks[i - 1].finished) {
                            Text(
                                text = "✔",
                                color = Color.Green,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(Modifier.width(10.dp))
                    DisplayTask(subject.tasks[i - 1].task, Color.Black, onEditTask = onEditTask, onDeleteTask = onDeleteTask)
                }
            }
            Spacer(Modifier.height(15.dp))
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
        Row() {
            Text(DateTimeFormat(task.deadline))
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
        Popup(onDismissRequest = {showDatePicker = false}, alignment = Alignment.TopStart){
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
fun AddTaskDialog(

    subject: Subject,
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
                var name by remember { mutableStateOf( "Subject Name") }
                var date by rememberSaveable { mutableStateOf(LocalDateTime.now()) }
//                var time by rememberSaveable { mutableStateOf<TimePicker?>(null) }
//
                var timePickerState: TimePickerState

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Task Name") },
                    singleLine = true
                )

                Spacer(Modifier.height(15.dp))
                DatePickerDocked({ date = it })
                timePickerState = TimePicker()
                Row(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        onClick = {

                            subjectViewModel.AddTask(
                                name,
                                date.withHour(timePickerState.hour).withMinute(timePickerState.minute),
                                subject
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
                var date by rememberSaveable { mutableStateOf(task.deadline) }

                var timePickerState : TimePickerState

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Task Name") },
                    singleLine = true
                )


                DatePickerDocked({ date = it })
                timePickerState = TimePicker()

                Spacer(Modifier.height(15.dp))
                DatePickerDocked({ date = it })
                Row(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        onClick = {

                            subjectViewModel.EditTask(
                                task,
                                name,
                                date.withHour(timePickerState.hour).withMinute(timePickerState.minute)
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
fun ToDoPage(modifier: Modifier) {
    val viewModel: SubjectViewModel = viewModel()

    var addSubjScreen by remember { mutableStateOf(false)}

    var addTaskScreen by remember { mutableStateOf<Subject?>(null) }
    var editTaskScreen by remember { mutableStateOf<Task?>(null) }
    var editSubjectScreen by remember { mutableStateOf<Subject?>(null) }
    var deleteTaskScreen by remember { mutableStateOf<Task?>(null) }
    var deleteSubjScreen by remember {mutableStateOf<Subject?>(null) }

    val state by viewModel.subjects.collectAsStateWithLifecycle()

    var dateTime by remember() { mutableStateOf(LocalDateTime.now()) }

    //basically a coroutine
    LaunchedEffect(Unit) {
        while (true) {
            dateTime = LocalDateTime.now()
            delay(1000L.milliseconds)
        }
    }

    Column(Modifier.fillMaxWidth()) {

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
            onAddTask = {subj : Subject -> addTaskScreen = subj},
            onEditSubject = { subj: Subject -> editSubjectScreen = subj },
            onDeleteSubject = { subj: Subject -> deleteSubjScreen = subj},
            onDeleteTask = { task : Task -> deleteTaskScreen = task})
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
    addTaskScreen?.let{AddTaskDialog(onDismiss = { addTaskScreen = null},
        subjectViewModel = viewModel,
        subject = it,
    )}
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






//@Preview(showBackground = true)
//@Composable
//private fun ToDoPagePreview(){
//  //  val viewModel = ToDoPage(Modifier)
////    MaterialTheme{
////        ToDoPage(Modifier.background(Color.White))
////    }
//}