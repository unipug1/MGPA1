package com.nyp.scaffoldingapp.ui.pages

data class Group(
    val groupID: String,
    val groupName: String,
    val yearOfStudy: String,
    val memberCount: Int,
    val capacity: Int,
    val ownerName: String,
    val privacyStatus: String,

    // discover group info
    val dailyGoalTime: Long = 0L,
    val totalStudyTime: Long = 0L,
    // val attendanceRate: Int = 0,
    val description: String = "",
    val creationDate: Long = System.currentTimeMillis()
)

data class GroupNotification(
    val notificationID: String,
    val groupID: String,
    val message: String,
    val date: Long
)

data class ChatMessage(
    val messageID: String,
    val groupID: String,
    val senderName: String,
    val message: String,
    val timestamp: Long,
    val isMine: Boolean
)

data class GroupStudyRecord(
    val groupID: String,
    val memberID: String,
    val nickname: String,
    val yearOfStudy: String,
    val date: String,
    val studyTimeSeconds: Long,
    val dailyGoalSeconds: Long
)