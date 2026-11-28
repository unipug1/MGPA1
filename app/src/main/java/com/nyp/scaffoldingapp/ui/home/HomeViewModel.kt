package com.nyp.scaffoldingapp.ui.home

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
    private val breakLogs = mutableListOf<BreakLog>()
    private var timerJob: Job? = null
    private var breakJob: Job? = null
    private var focusStartedAtMillis = 0L
    private var breakStartedAtMillis: Long? = null
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
        val state = _uiState.value as? HomeUIState.Success ?: return
        if (state.activeSubject?.id == id) return

        val subject = state.subjects.firstOrNull { it.id == id } ?: return

        _uiState.value = state.copy(
            subjects = state.subjects.filterNot { it.id == id },
            deletedSubjectStudySeconds =
                state.deletedSubjectStudySeconds + subject.timeSpentSeconds
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

    fun startTimer(subject: Subject) {
        val state = _uiState.value as? HomeUIState.Success ?: return
        if (state.activeSubject != null) return

        val selected = state.subjects.firstOrNull {
            it.id == subject.id && !it.isArchived
        } ?: return

        val now = SystemClock.elapsedRealtime()
        val breakSeconds = breakStartedAtMillis?.let { (now - it) / 1_000L }
        breakStartedAtMillis = null
        focusStartedAtMillis = now
        breakJob?.cancel()
        breakJob = null

        _uiState.value = state.copy(
            activeSubject = selected,
            isTimerRunning = true,
            currentFocusTime = 0L,
            currentRestTime = state.currentRestTime + (breakSeconds ?: 0L),
            pendingBreakSeconds = breakSeconds?.takeIf { it > 0L }
        )

        timerJob = viewModelScope.launch {
            while (true) {
                delay(250L)
                val current = _uiState.value as? HomeUIState.Success ?: break
                if (!current.isTimerRunning) break

                val elapsed = (SystemClock.elapsedRealtime() - focusStartedAtMillis) / 1_000L
                if (elapsed != current.currentFocusTime) {
                    _uiState.value = current.copy(currentFocusTime = elapsed)
                }
            }
        }
    }

    fun stopForBreak() {
        val state = _uiState.value as? HomeUIState.Success ?: return
        val subjectId = state.activeSubject?.id ?: return
        val now = SystemClock.elapsedRealtime()
        val focusSeconds = (now - focusStartedAtMillis) / 1_000L

        timerJob?.cancel()
        timerJob = null
        breakStartedAtMillis = now

        _uiState.value = state.copy(
            subjects = state.subjects.map { subject ->
                if (subject.id == subjectId) {
                    subject.copy(timeSpentSeconds = subject.timeSpentSeconds + focusSeconds)
                }
                else {
                    subject
                }
            },
            activeSubject = null,
            isTimerRunning = false,
            currentFocusTime = 0L,
            pendingBreakSeconds = null,
            currentBreakSeconds = 0L
        )

        breakJob?.cancel()
        breakJob = viewModelScope.launch {
            while (true) {
                delay(250L)

                val current = _uiState.value as? HomeUIState.Success ?: break
                if (current.activeSubject != null) break

                val elapsed = (SystemClock.elapsedRealtime() - now) / 1_000L
                if (elapsed != current.currentBreakSeconds) {
                    _uiState.value = current.copy(currentBreakSeconds = elapsed)
                }
            }
        }
    }

    fun dismissBreakLog(tag: String? = null) {
        val state = _uiState.value as? HomeUIState.Success ?: return
        val seconds = state.pendingBreakSeconds ?: return

        if (tag != null) {
            breakLogs.add(BreakLog(durationSeconds = seconds, tag = tag))
        }

        _uiState.value = state.copy(pendingBreakSeconds = null)
    }
}