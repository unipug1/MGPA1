package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
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
                    activeSubject = fetchedSubjects.first()
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
            id = "4",
            name = trimmedName,
            0,
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

    fun toggleTimer() {
        val currentState = _uiState.value
        if (currentState is HomeUIState.Success) {
            _uiState.value = currentState.copy(isTimerRunning = !currentState.isTimerRunning)
        }
    }
}