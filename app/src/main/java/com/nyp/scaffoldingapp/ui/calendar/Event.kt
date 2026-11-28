package com.nyp.scaffoldingapp.ui.calendar


import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime


import java.time.LocalTime
import java.time.LocalDate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


import java.util.UUID

import androidx.lifecycle.ViewModel
import com.nyp.scaffoldingapp.ui.todo.Routine

data class Type(
    var name : String = "Default",
    var color: Color = Color.Red,
    val id : String = UUID.randomUUID().toString()
)

data object DefaultType {
    val type = Type()
}

class Event (
    var name : String,
    var wholeDay : Boolean,
    val routine : Routine = Routine(),

    var startTime: LocalTime,
    var endTime: LocalTime,
    var type : Type = DefaultType.type,
    val id : String = UUID.randomUUID().toString()
){
    init {
        routine.endDate?.let {
            if (it.atTime(endTime).isBefore(routine.startDate.atTime(startTime))) {
                routine.endDate = routine.startDate
                endTime = startTime
            }
        }
    }

    fun OnDay(dateTime: LocalDateTime) : Boolean{
        if (!dateTime.toLocalDate().isBefore(routine.startDate) && !dateTime.toLocalDate().isAfter(routine.endDate)){
            if (wholeDay)
                return true
            else {
                if (!dateTime.toLocalTime().isBefore(startTime) && !dateTime.toLocalTime().isAfter(endTime))
                    return true
            }

        }
        return false
    }
    fun OnDay(dateTime: LocalDate) : Boolean{
        return !dateTime.isBefore(routine.startDate) && !dateTime.isAfter(routine.endDate)
    }
}

fun SortEvents(eventList: List<Event>) : MutableList<Pair<Type, MutableList<Event>>>{
    val eventTypes = mutableListOf<Pair<Type, MutableList<Event>>>()
    val events = eventList
    events.forEach{ event->
        for (type in eventTypes){
            if (type.first.id == event.type.id){
                type.second.add(event)
                return@forEach
            }
        }
        eventTypes.add(Pair(event.type, mutableListOf(event)))
    }
    return eventTypes
}

data class EventList(
    val events : MutableList<Event> = mutableListOf<Event>()
)

class EventViewModel : ViewModel(){
    private val _events = MutableStateFlow(EventList())
    val events : StateFlow<EventList> = _events.asStateFlow()


    fun GetEvents(requirement: (Event)-> Boolean = {true}): MutableList<Event>{
        val events = _events.value.events
        val returnEvents: MutableList<Event> = mutableListOf()
        for (i in 1..events.size){
            val event = events[i - 1]
            if (requirement(event)){
                returnEvents.add(event)
            }
        }
        return returnEvents
    }

    fun AddEvent(event: Event){
        _events.update {
            it.copy(events = (it.events + event).toMutableList())
        }
    }


}