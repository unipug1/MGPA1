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
import java.util.UUID

data class Task(
    var name : String,
    var deadline : LocalDateTime,
    val id: String = UUID.randomUUID().toString()

)

data class Subject(
    var name : String = "Default",
    var color: Color = Color.White,
    val tasks : List<ToDoTask> = mutableListOf<ToDoTask>(),
    val id: String = UUID.randomUUID().toString()
)

data class ToDoTask(
    val task : Task,
    var finished : Boolean,
    val id: String = UUID.randomUUID().toString()
)

data class ToDoSubjects(
    val subjects : List<Subject> = listOf<Subject>()
)

fun DateTimeFormat(dateTime: LocalDateTime) : String{
    val formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd HH-mm-ss", Locale.getDefault())
    return dateTime.format(formatter)
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
                    if (toDoTask.task.id == task.task.id) {
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

    fun EditTask(task: Task, subject: Subject, name: String, date: LocalDateTime){

    }
    fun EditSubject(subject : Subject, color : Color? = null, name : String? = null){
        _subjects.update { currentState ->
            val updatedSubjects = currentState.subjects.map{
                thisSubject ->
                if (subject.id == thisSubject.id){
                    val newName = if (name != null) name else thisSubject.name
                    val newColor = if (color != null) color else thisSubject.color
                    thisSubject.copy(name = newName, color = newColor)
                }
                else {
                    thisSubject
                }
            }
            ToDoSubjects(subjects = updatedSubjects)
        }
    }

    fun AddTask(name : String, deadline: LocalDateTime, subject : Subject) {
        val newTask = Task(name, deadline)
        val newToDoTask = ToDoTask(newTask, false)


        _subjects.update { currentState -> ToDoSubjects((currentState.subjects.map { thisSubject -> if (thisSubject.id == subject.id) {
            thisSubject.copy(tasks = thisSubject.tasks + newToDoTask)
        } else thisSubject }) ) }


    }

    fun GetSubjects() : ToDoSubjects{
        return subjects.value
    }
}