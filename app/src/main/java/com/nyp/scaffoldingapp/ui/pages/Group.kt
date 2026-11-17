package com.nyp.scaffoldingapp.ui.pages

data class Group(
    val groupID: String,
    val groupName: String,
    val yearOfStudy: String,
    val memberCount: Int,
    val capacity: Int,
    val ownerName: String,
    val privacyStatus: String
)

data class GroupNotification(
    val notificationID: String,
    val groupID: String,
    val message: String,
    val date: Long
)