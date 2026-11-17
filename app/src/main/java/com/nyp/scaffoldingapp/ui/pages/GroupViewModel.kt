package com.nyp.scaffoldingapp.ui.pages

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    // read only:
    val groups = _groups.asStateFlow()
    val notifications = _notifications.asStateFlow()

    fun clearNotifications() {
        _notifications.value = emptyList()
    }
}