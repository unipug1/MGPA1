package com.nyp.scaffoldingapp.ui.ranking

data class RankingStudyRecord(
    val userID: String,
    val userName: String,
    val yearOfStudy: String,
    val date: String,
    val durationSeconds: Long
)

data class RankingEntry(
    val userID: String,
    val userName: String,
    val yearOfStudy: String,
    val totalDurationSeconds: Long
)