package com.nyp.scaffoldingapp.ui.home

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
        val pendingBreakSeconds: Long? = null,
        val currentBreakSeconds: Long = 0L,
        val deletedSubjectStudySeconds: Long = 0L
    ) : HomeUIState
    data class Error(val message: String) : HomeUIState
}