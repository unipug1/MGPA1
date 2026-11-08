package com.nyp.scaffoldingapp.ui.KaiserClasses

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime


import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.LocalTime
import java.time.LocalDate

import java.text.SimpleDateFormat
import java.util.Calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Task(
    var name : String,
    var deadline : LocalDateTime

)

data class Subject(
    var name : String = "Default",
    var color: Color = Color.White,
    val tasks : List<ToDoTask> = mutableListOf<ToDoTask>()
)

data class ToDoTask(
    val task : Task,
    var finished : Boolean
)

data class ToDoSubjects(
    val subjects : List<Subject> = listOf<Subject>()
)

fun DateTimeFormat(dateTime: LocalDateTime) : String{
    //val formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd HH-mm-ss", Locale.getDefault())
    return "1 Jan 23:59"
}

class SubjectViewModel : ViewModel(){
    private val _subjects = MutableStateFlow(ToDoSubjects())
    val subjects: StateFlow<ToDoSubjects> = _subjects.asStateFlow()

    fun AddSubject(name : String, color: Color){
        val newSubj = Subject(name, color)


        _subjects.update { currentState -> currentState.copy((currentState.subjects + newSubj)) }
    }

    fun SetTaskFinished(task: ToDoTask, finished: Boolean){
        _subjects.update { currentState ->
            val updatedSubjects = currentState.subjects.map { subject ->
                val updatedTasks = subject.tasks.map { toDoTask ->
                    if (toDoTask.task.name == task.task.name) {
                        toDoTask.copy(finished = finished)
                        //toDoTask
                    } else {
                        toDoTask
                    }
                }
                subject.copy(tasks = updatedTasks)
            }
            ToDoSubjects(subjects = updatedSubjects)
        }
    }

    fun AddTask(name : String, deadline: LocalDateTime, subject : Subject) {
        val newTask = Task(name, deadline)
        val newToDoTask = ToDoTask(newTask, false)


        _subjects.update { currentState -> ToDoSubjects((currentState.subjects.map { thisSubject -> if (thisSubject.name == subject.name) {
            thisSubject.copy(tasks = thisSubject.tasks + newToDoTask)
        } else thisSubject }) ) }


    }

    fun GetSubjects() : ToDoSubjects{
        return subjects.value
    }
}