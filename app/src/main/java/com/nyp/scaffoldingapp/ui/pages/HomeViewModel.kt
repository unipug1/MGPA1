package com.nyp.scaffoldingapp.ui.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import android.os.SystemClock
import java.util.UUID
import kotlinx.coroutines.Job

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
    private var timerJob: Job? = null
    private var phaseStartedAtMillis = 0L
    private var completedFocusMillis = 0L
    private var completedRestMillis = 0L
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    init {
        fetchSubjectsFromNetwork()
    }

    fun fetchSubjectsFromNetwork() {
        viewModelScope.launch {
            try {
                // replace with mock api dog
                val fetchedSubjects = listOf(
                    Subject("1", "English", 3600, "#FF5733"),
                    Subject("2", "Chinese", 0, "#33FF57")
                )
                _uiState.value = HomeUIState.Success(
                    subjects = fetchedSubjects,
                    currentFocusTime = 0,
                    currentRestTime = 0,
                    isTimerRunning = false,
                    activeSubject = null
                )
            }
            catch (e: Exception) {
                _uiState.value = HomeUIState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun addSubject(name: String, color: String) {
        val currentState = _uiState.value
        if (currentState !is HomeUIState.Success) return

        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return

        val newSubject = Subject(
            id = UUID.randomUUID().toString(),
            name = trimmedName,
            timeSpentSeconds = 0L,
            colorHex = color
        )

        _uiState.value = currentState.copy(
            subjects = currentState.subjects + newSubject
        )
    }

    fun moveSubject(fromIndex: Int, toIndex: Int) {
        val currentState = _uiState.value
        if (currentState !is HomeUIState.Success) return

        val mutableList = currentState.subjects.toMutableList()
        // move item in list
        val item = mutableList.removeAt(fromIndex)
        mutableList.add(toIndex, item)

        // update UI state with newly ordered list
        _uiState.value = currentState.copy(subjects = mutableList)
    }

    fun updateSubject(id: String, newName: String, newColor: String) {
        val currentState = _uiState.value
        if (currentState !is HomeUIState.Success) return

        val updatedList = currentState.subjects.map { subject ->
            if (subject.id == id) {
                subject.copy(name = newName, colorHex = newColor)
            } else {
                subject
            }
        }
        _uiState.value = currentState.copy(subjects = updatedList)
    }

    fun deleteSubject(id: String) {
        val currentState = _uiState.value
        if (currentState !is HomeUIState.Success) return
        // filter subject given id
        val updatedList = currentState.subjects.filter { it.id != id}
        _uiState.value = currentState.copy(subjects = updatedList)
    }

    fun startTimer(subject: Subject) {
        val state = _uiState.value as? HomeUIState.Success ?: return
        if (state.activeSubject != null) return

        val selected = state.subjects.firstOrNull {
            it.id == subject.id && !it.isArchived
        } ?: return

        completedFocusMillis = 0L
        completedRestMillis = 0L
        phaseStartedAtMillis = SystemClock.elapsedRealtime()

        _uiState.value = state.copy(
            activeSubject = selected,
            isTimerRunning = true,
            currentFocusTime = 0L
        )

        timerJob = viewModelScope.launch {
            while (true) {
                delay(250L)
                val current = _uiState.value as? HomeUIState.Success ?: break
                if (current.activeSubject == null) break

                val updated = readSessionTimes(current, SystemClock.elapsedRealtime())
                if (updated != current) _uiState.value = updated
            }
        }
    }

    fun toggleTimer() {
        val state = _uiState.value as? HomeUIState.Success ?: return
        if (state.activeSubject == null) return

        val now = SystemClock.elapsedRealtime()
        val updated = readSessionTimes(state, now)
        val elapsed = now - phaseStartedAtMillis

        if (state.isTimerRunning) {
            completedFocusMillis += elapsed
        } else {
            completedRestMillis += elapsed
        }

        phaseStartedAtMillis = now
        _uiState.value = updated.copy(isTimerRunning = !state.isTimerRunning)
    }

    fun finishTimer() {
        val state = _uiState.value as? HomeUIState.Success ?: return
        val subjectId = state.activeSubject?.id ?: return
        val updated = readSessionTimes(state, SystemClock.elapsedRealtime())

        timerJob?.cancel()
        timerJob = null

        _uiState.value = updated.copy(
            subjects = updated.subjects.map { subject ->
                if (subject.id == subjectId) {
                    subject.copy(
                        timeSpentSeconds = subject.timeSpentSeconds + updated.currentFocusTime
                    )
                } else {
                    subject
                }
            },
            activeSubject = null,
            isTimerRunning = false,
            currentFocusTime = 0L
        )
    }

    private fun readSessionTimes(
        state: HomeUIState.Success,
        now: Long
    ): HomeUIState.Success {
        val elapsed = now - phaseStartedAtMillis
        val focusMillis =
            completedFocusMillis + if (state.isTimerRunning) elapsed else 0L
        val restMillis =
            completedRestMillis + if (state.isTimerRunning) 0L else elapsed

        return state.copy(
            currentFocusTime = focusMillis / 1_000L,
            currentRestTime = restMillis / 1_000L
        )
    }

    fun setSubjectArchived(id: String, archived: Boolean) {
        val state = _uiState.value as? HomeUIState.Success ?: return
        if (state.activeSubject?.id == id) return

        _uiState.value = state.copy(
            subjects = state.subjects.map { subject ->
                if (subject.id == id) subject.copy(isArchived = archived)
                else subject
            }
        )
    }
}