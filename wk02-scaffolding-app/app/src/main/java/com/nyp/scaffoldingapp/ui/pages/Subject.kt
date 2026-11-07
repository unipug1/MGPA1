package com.nyp.scaffoldingapp.ui.pages

data class Subject(
    val id: String,
    val name: String,
    val timeSpentSeconds: Long,
    val colorHex: String
)

sealed interface HomeUIState {
    object Loading : HomeUIState
    data class Success(
        val subjects: List<Subject>,
        val currentFocusTime: Long,
        val isTimerRunning: Boolean,
        val activeSubject: Subject?
    ) : HomeUIState
    data class Error(val message: String) : HomeUIState
}