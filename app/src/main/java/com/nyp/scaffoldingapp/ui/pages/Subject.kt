package com.nyp.scaffoldingapp.ui.pages

data class Subject(
    val id: String,
    val name: String,
    val timeSpentSeconds: Long,
    val colorHex: String,
    val isArchived: Boolean = false
)

data class BreakLog(
    val durationSeconds: Long,
    val tag: String
)

sealed interface HomeUIState {
    object Loading : HomeUIState
    data class Success(
        val subjects: List<Subject>,
        val currentFocusTime: Long,
        val currentRestTime: Long,
        val isTimerRunning: Boolean,
        val activeSubject: Subject?,
        val pendingBreakSeconds: Long? = null
    ) : HomeUIState
    data class Error(val message: String) : HomeUIState
}