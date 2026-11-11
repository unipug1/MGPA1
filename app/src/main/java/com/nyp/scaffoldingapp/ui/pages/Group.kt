package com.nyp.scaffoldingapp.ui.pages

data class Group(
    val groupName: String,
    val groupID: String,
    val yearOfStudy: String,
    val dailyGoalTime: Long,
    val capacity: Int,
    val currentMemberCount: Int,
    val description: String,
    val creationDate: String,
    val ownerName: String,
    val privacyStatus: String
)

// for chat messages
data class ChatMessage(
    val id: String,
    val senderName: String,
    val messageText: String
)

// add profile pic later + allowed apps?
data class Member(
    val name: String,
    val userID: String,
    // val profilePicURL: String,
    val bio: String,
    val statusMessage: String,
    val currentFocusTime: Long,
    val maxFocusTime: Long,
    val allowedApps: List<String>
)

data class AttendanceRecord(
    val date: String,
    val studiedDays: List<Boolean>,
    val attendancePercentage: Float
)

data class RankingItem(
    val name: String,
    val userRank: Int,
    val yearOfStudy: Int,
    val timeFocused: Long
    // or get user id to fetch info
)