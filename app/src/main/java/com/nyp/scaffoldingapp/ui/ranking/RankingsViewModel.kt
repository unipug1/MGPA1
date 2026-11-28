package com.nyp.scaffoldingapp.ui.ranking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RankingsViewModel : ViewModel() {
    //private val _studyRecords = MutableStateFlow<List<RankingStudyRecord>>(emptyList())
    private val _studyRecords = MutableStateFlow(
        listOf(
            RankingStudyRecord(
                "1", "Student A", "Poly & ITE",
                "2026-11-22", 7200L
            ),
            RankingStudyRecord(
                "2", "Student B", "JC Year 2",
                "2026-11-22", 3600L
            ),
            RankingStudyRecord(
                "1", "Student A", "Poly & ITE",
                "2026-11-22", 1800L
            ),
            RankingStudyRecord(
                "3", "Student C", "Poly & ITE",
                "2026-11-21", 5400L
            )
        )
    )

    // read only
    val studyRecords = _studyRecords.asStateFlow()
}