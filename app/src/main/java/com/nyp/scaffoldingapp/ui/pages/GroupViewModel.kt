package com.nyp.scaffoldingapp.ui.pages

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                privacyStatus = "Private"
            ),
            Group(
                groupID = "group2",
                groupName = "The 4 dq pros",
                yearOfStudy = "Poly & ITE",
                memberCount = 4,
                capacity = 4,
                ownerName = "_sze:(",
                privacyStatus = "Public"
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
                message = "Your request has been approved.",
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

    // read only:
    val groups = _groups.asStateFlow()
    val notifications = _notifications.asStateFlow()
    val messages = _messages.asStateFlow()

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
}