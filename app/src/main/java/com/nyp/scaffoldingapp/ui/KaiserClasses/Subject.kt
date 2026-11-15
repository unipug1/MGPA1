package com.nyp.scaffoldingapp.ui.KaiserClasses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import java.time.DayOfWeek
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjuster
import java.util.UUID
import java.time.temporal.TemporalAdjusters


enum class TaskType{
    DEFAULT,
    TIME_RANGE
}

enum class RoutineType{
    NONE,
    DAILY,
    WEEKLY
}

sealed interface Edit<out T>{
    data object Keep : Edit<Nothing>
    data class Change<T>(val value : T) : Edit<T>
}

fun <T> Edit<T>.ApplyChange(current : T): T {
    return when(this){
        Edit.Keep -> current
        is Edit.Change -> value
    }
}

enum class CompletionProgress{
    NOT_DONE,
    PARTIALLY_DONE,
    DONE
}

class Routine(
    var days: MutableList<DayOfWeek> = mutableListOf(),
    var routineType: RoutineType = RoutineType.NONE,
    var frequency : Int = 0,
    val startDate : LocalDate = LocalDate.now()
){
    fun OnDay(dateTime : LocalDate): Boolean{
        if (routineType == RoutineType.WEEKLY) {
            if (!(dateTime.dayOfWeek in days))
                return false
            val monOfRoutineWk = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val monOfDate = dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val weeksApart : Int = ChronoUnit.WEEKS.between(monOfRoutineWk, monOfDate).toInt()

            if (weeksApart % frequency != 0)
                return false

            return true
        }
        else if (routineType == RoutineType.DAILY){
            val daysApart : Int = ChronoUnit.DAYS.between(startDate, dateTime).toInt()
            if (daysApart % frequency == 0 && dateTime.dayOfWeek in days)
                return true
        }
        return false


    }
}


class Task(
    var name : String,
    val id: String = UUID.randomUUID().toString(),
    var completionProgress: CompletionProgress = CompletionProgress.NOT_DONE,

    var duration: Duration? = null,
    var time: LocalDateTime? = null,
    var routine : Routine
) {

    fun Edit(name : Edit<String> = Edit.Keep,
             progress : Edit<CompletionProgress> = Edit.Keep,
             time : Edit<LocalDateTime?> = Edit.Keep,
             duration: Edit<Duration?> = Edit.Keep,
             routine: Edit<Routine> = Edit.Keep) : Task{
        name.ApplyChange(this.name)
        progress.ApplyChange(this.completionProgress)
        time.ApplyChange(this.time)
        duration.ApplyChange(this.duration)
        routine.ApplyChange(this.routine)
        return this
    }



}



data class Subject(
    var name : String = "Default",
    var color: Color = Color.White,
    val tasks : List<Task> = mutableListOf<Task>(),
    val id: String = UUID.randomUUID().toString()
)



data class ToDoSubjects(
    val subjects : List<Subject> = listOf<Subject>()
)

fun DateTimeFormat(dateTime: LocalDateTime) : String{
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm", Locale.getDefault())
    return dateTime.format(formatter)
}

fun TimeFormat(time: LocalDateTime, format: String = "yyyy-MM-dd HH-mm") : String{
    val formatter = DateTimeFormatter.ofPattern(format, Locale.getDefault())
    return time.format(formatter)
}



class SubjectViewModel : ViewModel(){

    data class TaskSubj(
        var Task: Task,
        var subject: Subject
    )

    private val _subjects = MutableStateFlow(ToDoSubjects())
    val subjects: StateFlow<ToDoSubjects> = _subjects.asStateFlow()

    fun AddSubject(name : String, color: Color){
        val newSubj = Subject(name, color)


        _subjects.update { currentState -> currentState.copy((currentState.subjects + newSubj)) }
    }

    fun GetTasks(requirement: (Task)-> Boolean = {true}): MutableList<TaskSubj>{
        val subjects = _subjects.value.subjects
        val returnTasks: MutableList<TaskSubj> = mutableListOf()
        for (i in 1..subjects.size){
            val subject = subjects[i - 1]
            for (i in 1..subject.tasks.size){
                val task = subject.tasks[i - 1]
                if (requirement(task))
                    returnTasks.add(TaskSubj(task, subject))
            }
        }
        return returnTasks
    }

    fun GetSubjects(requirement: (Subject)-> Boolean = {true}): MutableList<Subject>{
        val subjects = _subjects.value.subjects
        val returnSubjs: MutableList<Subject> = mutableListOf()
        for (i in 1..subjects.size){
            val subject = subjects[i - 1]
            if (requirement(subject))
                returnSubjs.add(subject)
        }
        return returnSubjs
    }

    fun DeleteTask(task: Task){
        _subjects.update{ currentState ->
            currentState.copy(subjects = currentState.subjects.map{
                subject -> subject.copy(tasks = subject.tasks.filterNot{it.id == task.id})
            })

            //ToDoSubjects(subjects = updatedSubjects)
        }
    }

    fun DeleteSubject(subject: Subject){
        _subjects.update{ currentState ->
            var subjToDelete: Subject? = null
            val updatedSubjects = currentState.subjects.toMutableList()
            for (i in 1..updatedSubjects.size)
                if (subject.id == updatedSubjects[i - 1].id) {
                    subjToDelete = updatedSubjects[i - 1]
                }

            updatedSubjects.remove(subjToDelete)

            ToDoSubjects(subjects = updatedSubjects)
        }
    }

    fun SetTaskFinished(task: Task, finished: CompletionProgress){
        _subjects.update { currentState ->
            val updatedSubjects = currentState.subjects.map { subject ->
                val updatedTasks = subject.tasks.map { thisTask ->
                    if (thisTask.id == task.id) {
                        thisTask.Edit(progress = Edit.Change(finished))
                        //Task
                    } else {
                        thisTask
                    }
                }
                subject.copy(tasks = updatedTasks)
            }
            ToDoSubjects(subjects = updatedSubjects)
        }
    }
    fun SetTaskFinished(requirement: (Task)->Boolean, finished: CompletionProgress){
        _subjects.update { currentState ->
            val updatedSubjects = currentState.subjects.map { subject ->
                val updatedTasks = subject.tasks.map { thisTask ->
                    if (requirement(thisTask)) {
                        thisTask.Edit(progress = Edit.Change(finished))
                        //Task
                    } else {
                        thisTask
                    }
                }
                subject.copy(tasks = updatedTasks)
            }
            ToDoSubjects(subjects = updatedSubjects)
        }
    }

    fun EditTask(task: Task, name : Edit<String> = Edit.Keep,
                 progress : Edit<CompletionProgress> = Edit.Keep,
                 time : Edit<LocalDateTime?> = Edit.Keep,
                 duration: Edit<Duration?> = Edit.Keep,
                 routine: Edit<Routine> = Edit.Keep){
        _subjects.update { currentState ->
            val updatedSubjects = currentState.subjects.map{
                    thisSubject ->
                val updatedTasks = thisSubject.tasks.map{ thisTask->
                    if (thisTask.id == task.id){

                        thisTask.Edit(name = name, time = time, progress = progress, duration = duration)

                    }
                    else
                        thisTask

                }
                thisSubject.copy(tasks = updatedTasks)
            }
            ToDoSubjects(subjects = updatedSubjects)
        }
    }
    fun EditSubject(subject : Subject, color : Color? = null, name : String? = null){
        _subjects.update { currentState ->
            val updatedSubjects = currentState.subjects.map{
                thisSubject ->
                if (subject.id == thisSubject.id){
                    val newName = name?.let{name}?:subject.name
                    val newColor = color?.let{color}?:subject.color
                    thisSubject.copy(name = newName, color = newColor)
                }
                else {
                    thisSubject
                }
            }
            ToDoSubjects(subjects = updatedSubjects)
        }
    }

    fun AddTask(name : String, subject : Subject,time: LocalDateTime? = null, duration: Duration? = null, routine: Routine = Routine()) {
        val newTask = Task(name = name, time = time, duration = duration, routine = routine, completionProgress = CompletionProgress.NOT_DONE)


        _subjects.update { currentState -> ToDoSubjects((currentState.subjects.map { thisSubject -> if (thisSubject.id == subject.id) {
            thisSubject.copy(tasks = thisSubject.tasks + newTask)
        } else thisSubject }) ) }


    }
    fun AddTask(subject: Subject, task: Task){


        _subjects.update { currentState -> ToDoSubjects((currentState.subjects.map { thisSubject -> if (thisSubject.id == subject.id) {
            thisSubject.copy(tasks = thisSubject.tasks + task)
        } else thisSubject }) ) }

    }
}

//class EventViewModel :