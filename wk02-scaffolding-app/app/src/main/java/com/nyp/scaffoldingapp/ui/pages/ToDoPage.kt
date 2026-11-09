package com.nyp.scaffoldingapp.ui.pages

import android.R
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

//@Composable


@Composable
fun TasksDisplay(subjects: ToDoSubjects, modifier: Modifier, viewModel : SubjectViewModel){


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
                Text(subject.name, color = subject.color)
                Spacer(Modifier.width(15.dp))
                val checkList = subject.tasks
                Button(onClick = { viewModel.AddTask("Test", LocalDateTime.now(), subject)}, modifier = Modifier
                    .size(20.dp),shape = RoundedCornerShape(3.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),border = BorderStroke(color = Color.White, width = 2.dp), contentPadding = PaddingValues(0.dp)){Text("+")}
                Spacer(Modifier.width(2.dp))
                Button(
                    onClick = {},
                    elevation = null,
                    modifier = Modifier
                        .width(15.dp)
                        .height(20.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) { Text(":") }

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
                            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(4.dp))
                            .background(Color.LightGray)
                            .clickable {
                                viewModel.SetTaskFinished(subject.tasks[i - 1], !subject.tasks[i - 1].finished)
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
                    DisplayTask(subject.tasks[i - 1].task, Color.Black)
                }
            }
            Spacer(Modifier.height(15.dp))
        }
    }
}
@Composable
fun DisplayTask(task : Task, color: Color, modifier: Modifier = Modifier){
    Row(modifier){

        Text(task.name, color = color)
        Spacer(Modifier.weight(1f))
        Row() {
            Text(DateTimeFormat(task.deadline))
            Spacer(Modifier.width(2.dp))
            Button(
                onClick = {},
                elevation = null,
                modifier = Modifier
                    .width(15.dp)
                    .height(20.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) { Text(":") }
            Spacer(Modifier.width(4.dp))
        }
    }
}


@Composable
fun ToDoPage(modifier : Modifier){
    val viewModel : SubjectViewModel = viewModel()

    val state by viewModel.subjects.collectAsStateWithLifecycle()

    var dateTime by remember() { mutableStateOf(LocalDateTime.now()) }

    //basically a coroutine
    LaunchedEffect(Unit) {
        while (true){
            dateTime = LocalDateTime.now()
            delay(1000L.milliseconds)
        }
    }

    Column(Modifier.fillMaxWidth()){

        Column(Modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .border(color = Color(0xFF645f6b), width = 2.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            Spacer(Modifier.height(5.dp))

            val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            Text(dateTime.format(dateFormatter), style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(5.dp))
            Text(dateTime.format(timeFormatter), style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(5.dp))
        Row(){
            Spacer(Modifier.width(15.dp))
            Button(onClick = { viewModel.AddSubject("Test", Color.Black)}, modifier = Modifier
                .size(30.dp),shape = RoundedCornerShape(3.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Black),border = BorderStroke(color = Color.Black, width = 2.dp), contentPadding = PaddingValues(0.dp)){Text(text = "+", fontSize = 24.sp)}
            Spacer(Modifier.weight(1f))
        }
        Spacer(Modifier.height(5.dp))

        TasksDisplay(subjects = state, modifier, viewModel)
    }


}

//@Composable


@Preview(showBackground = true)
@Composable
private fun ToDoPagePreview(){
  //  val viewModel = ToDoPage(Modifier)
    MaterialTheme{
        ToDoPage(Modifier.background(Color.White))
    }
}