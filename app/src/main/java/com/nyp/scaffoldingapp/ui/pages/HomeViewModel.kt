package com.nyp.scaffoldingapp.ui.pages


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
    private var startedAtMillis: Long? = null
    private var pausedAtMillis: Long? = null
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
        val state = _uiState.value as? HomeUIState.Success ?: return
        val startTime = startedAtMillis ?: return
        val now = System.currentTimeMillis()


        if (state.isTimerRunning) {
            pausedAtMillis = now
            _uiState.value = state.copy(
                isTimerRunning = false,
                currentFocusTime = (now - startTime).coerceAtLeast(0L) / 1000L
            )
        } else {
            val pausedAt = pausedAtMillis ?: return
            startedAtMillis = startTime + (now - pausedAt)
            pausedAtMillis = null
            _uiState.value = state.copy(isTimerRunning = true)
        }
    }


    fun startTimer(subject: Subject) {
        val currentState = _uiState.value
        if (currentState !is HomeUIState.Success) return


        if (startedAtMillis != null) return


        startedAtMillis = System.currentTimeMillis()
        _uiState.value = currentState.copy(
            activeSubject = subject,
            isTimerRunning = true,
            currentFocusTime = 0L)


        val startTime = startedAtMillis ?: return
        viewModelScope.launch {
            while (true) {
                delay(1000L)


                val startTime = startedAtMillis ?: break
                val state = _uiState.value as? HomeUIState.Success ?: break
                if (!state.isTimerRunning) continue


                val elapsedSeconds = (System.currentTimeMillis() - startTime).coerceAtLeast(0L) / 1000L


                _uiState.value = state.copy(currentFocusTime = elapsedSeconds)
            }
        }
    }
}