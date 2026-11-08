package com.nyp.scaffoldingapp.ui.pages


import androidx.compose.runtime.Composable
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




    fun toggleTimer() {
        val currentState = _uiState.value
        if (currentState is HomeUIState.Success) {
            _uiState.value = currentState.copy(isTimerRunning = !currentState.isTimerRunning)
        }
    }
}
