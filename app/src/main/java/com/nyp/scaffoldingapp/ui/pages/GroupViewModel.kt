package com.nyp.scaffoldingapp.ui.pages

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import java.util.UUID

class GroupViewModel : ViewModel() {
    private val _groups = MutableStateFlow(
        listOf(
            Group(
                groupID = "group1",
                groupName = "Lock In",
                yearOfStudy = "Poly & ITE",
                memberCount = 3,
                capacity = 3,
                ownerName = "_1ucky",
                privacyStatus = "Private",
                dailyGoalTime = 7200L
            ),
            Group(
                groupID = "group2",
                groupName = "The 4 dq pros",
                yearOfStudy = "Poly & ITE",
                memberCount = 4,
                capacity = 4,
                ownerName = "_sze:(",
                privacyStatus = "Public",
                isOwnedByMe = true
            )
        )
    )

    private val _discoverGroups = MutableStateFlow(
        listOf(
            Group(
                groupID = "group3",
                groupName = "psyducky",
                yearOfStudy = "Poly & ITE",
                memberCount = 2,
                capacity = 5,
                ownerName = "psyduck",
                privacyStatus = "Private",
                password = "secret",
                dailyGoalTime = 7200L,
                totalStudyTime = 0L,
                description = "LOCK INNN",
                creationDate = System.currentTimeMillis()
            ),
            Group(
                groupID = "group4",
                groupName = "doofy",
                yearOfStudy = "Poly & ITE",
                memberCount = 3,
                capacity = 3,
                ownerName = "_sze:(",
                privacyStatus = "Public",
                dailyGoalTime = 3600L,
                totalStudyTime = 360L,
                description = "LOCK OUT",
                creationDate = System.currentTimeMillis() - 86_400_000L,
            )
        )
    )

    private val _notifications = MutableStateFlow(
        listOf(
            GroupNotification(
                notificationID = UUID.randomUUID().toString(),
                groupID = "group1",
                message = "_1ucky nudged sze:(",
                date = System.currentTimeMillis()
            ),
            GroupNotification(
                notificationID = UUID.randomUUID().toString(),
                groupID = "group1",
                message = "sze:( nudged kar",
                date = System.currentTimeMillis()
            )
        )
    )

    private val _messages = MutableStateFlow(
        listOf(
            ChatMessage(
                messageID = "msg1",
                groupID = "group1",
                senderName = "_1ucky",
                message = "Hello everyone!",
                timestamp = System.currentTimeMillis(),
                isMine = false
            ),
            ChatMessage(
                messageID = "msg2",
                groupID = "group1",
                senderName = "Me",
                message = "Hi!",
                timestamp = System.currentTimeMillis(),
                isMine = true
            ),
            ChatMessage(
                messageID = "msg3",
                groupID = "group2",
                senderName = "_sze:(",
                message = "Lock in",
                timestamp = System.currentTimeMillis(),
                isMine = false
            )
        )
    )

    private val _studyRecords = MutableStateFlow(
        listOf(
            GroupStudyRecord(
                groupID = "group1",
                memberID = "member1",
                nickname = "sze:(",
                yearOfStudy = "JC Year 2",
                date = "2026-11-16",
                studyTimeSeconds = 588L,
                dailyGoalSeconds = 10800L
            ),
            GroupStudyRecord(
                groupID = "group1",
                memberID = "member1",
                nickname = "sze:(",
                yearOfStudy = "JC Year 2",
                date = "2026-11-17",
                studyTimeSeconds = 7200L,
                dailyGoalSeconds = 10800L
            ),
        )
    )

    private val _groupChatEnabled = MutableStateFlow<Map<String, Boolean>>(
        emptyMap()
    )

    // read only:
    val groups = _groups.asStateFlow()
    val discoverGroups = _discoverGroups.asStateFlow()
    val notifications = _notifications.asStateFlow()
    val messages = _messages.asStateFlow()
    val studyRecords = _studyRecords.asStateFlow()
    val groupChatEnabled = _groupChatEnabled.asStateFlow()

    fun clearNotifications() {
        _notifications.value = emptyList()
    }

    fun sendMessage(groupID: String, messageText: String) {
        if (messageText.isBlank()) return

        val newMessage = ChatMessage(
            messageID = UUID.randomUUID().toString(),
            groupID = groupID,
            senderName = "Me",
            message = messageText.trim(),
            timestamp = System.currentTimeMillis(),
            isMine = true
        )

        _messages.value += newMessage
    }

    fun createGroup(group: Group) {
        val ownedGroup = group.copy(isOwnedByMe = true)
        _groups.value += ownedGroup
    }

    fun joinGroup(group: Group): Boolean {
        val currentGroups = _groups.value

        // prevent joining a group the user already belongs to
        if (currentGroups.any { it.groupID == group.groupID }) return false

        // check whether the group still exists in Discover Groups
        val discoverGroup = _discoverGroups.value.firstOrNull {
            it.groupID == group.groupID } ?: return false

        // prevent joining full groups
        if (discoverGroup.memberCount >= discoverGroup.capacity) return false

        // increase member count after joining
        val joinedGroup = discoverGroup.copy(
            memberCount = discoverGroup.memberCount + 1
        )

        // add joined group to my groups
        _groups.value = currentGroups + joinedGroup

        // remove joined group from discovery page
        _discoverGroups.value = _discoverGroups.value.filterNot {
            it.groupID == joinedGroup.groupID
        }

        return true
    }

    fun updateGroup(updatedGroup: Group) {
        _groups.value = _groups.value.map { group ->
            if (group.groupID == updatedGroup.groupID) {
                updatedGroup
            } else {
                group
            }
        }
        _discoverGroups.value = _discoverGroups.value.map { group ->
            if (group.groupID == updatedGroup.groupID) {
                updatedGroup
            } else {
                group
            }
        }
    }

    fun disbandGroup(groupID: String) {
        _groups.value = _groups.value.filterNot {
            it.groupID == groupID && it.isOwnedByMe
        }
        _discoverGroups.value = _discoverGroups.value.filterNot {
            it.groupID == groupID && it.isOwnedByMe
        }
    }

    fun leaveGroup(groupID: String) {
        val group = _groups.value.firstOrNull {
            it.groupID == groupID
        } ?: return

        // group leader cannot leave own group
        if (group.isOwnedByMe) return

        val updatedGroup = group.copy(
            memberCount = (group.memberCount - 1).coerceAtLeast(0)
        )
        // remove from current user's joined groups
        _groups.value = _groups.value.filterNot {
            it.groupID == groupID
        }
        // update the group in discovery groups
        _discoverGroups.value = _discoverGroups.value.map {
            if (it.groupID == groupID) {
                updatedGroup
            } else {
                it
            }
        }
    }

    fun addStudyRecord(studyRecord: GroupStudyRecord) {
        _studyRecords.value += studyRecord
    }

    fun updateGroupChatEnabled(groupID: String, enabled: Boolean) {
        _groupChatEnabled.value += (groupID to enabled)
    }
}