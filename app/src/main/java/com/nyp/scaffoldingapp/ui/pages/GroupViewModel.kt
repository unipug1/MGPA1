package com.nyp.scaffoldingapp.ui.pages

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GroupViewModel : ViewModel() {
    private val _groups = MutableStateFlow<List<Group>>(
        listOf(
            Group("testGroup", "1", "JC Year 2", 3600, 3, 2, "test",
                "01-01-2022", "tester", "public"),
            Group("testGroup2", "2", "Poly", 2600, 3, 2, "test",
                "01-01-2022", "tester", "private")
        )
    )

    // key is a group id, value is group list of messages
    private val _messagesByGroup = MutableStateFlow<Map<String, List<ChatMessage>>>(
        mapOf(
            "1" to listOf(ChatMessage("1", "tester", "", "hello")),
            "2" to listOf(ChatMessage("2", "tester", "", "testing"))
        )
    )

    // key is group id, value is selected option
    private val _notificationSettings = MutableStateFlow<Map<String, GroupNotificationPreferences>>(emptyMap())
    private val _chatEnabled = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    val groups = _groups.asStateFlow()
    val messagesByGroup = _messagesByGroup.asStateFlow()
    val notificationSettings = _notificationSettings.asStateFlow()
    val chatEnabled = _chatEnabled.asStateFlow()

    // add groups
    fun addGroup(group: Group) {
        _groups.update { current ->
            if (current.any {it.groupID == group.groupID}) current
            else current + group
        }
    }

    // send message
    fun sendMessage(groupID: String, text: String) {
        val trimmedText = text.trim()
        if (trimmedText.isEmpty()) return
        // create message w unique id
        val newMessage = ChatMessage(
            "3", "Me", "", trimmedText
        )
        // append message + return updated map
        _messagesByGroup.update { current ->
            val updatedMessages = current[groupID].orEmpty() + newMessage
            current + (groupID to updatedMessages)
        }
    }

    // notification settings
    fun setNotificationSettings(groupID: String, option: String) {
        _notificationSettings.update { current ->
            val preferences = current[groupID] ?: GroupNotificationPreferences()
            current + (groupID to preferences.copy(groupChat = option))
        }
    }

    // notice settings
    fun setNoticeSettings(groupID: String, option: String) {
        _notificationSettings.update { current ->
            val preferences = current[groupID] ?: GroupNotificationPreferences()
            current + (groupID to preferences.copy(notice = option))
        }
    }

    // comment settings
    fun setCommentSettings(groupID: String, option: String) {
        _notificationSettings.update { current ->
            val preferences = current[groupID] ?: GroupNotificationPreferences()
            current + (groupID to preferences.copy(comment = option))
        }
    }

    // post settings
    fun setNewPostSettings(groupID: String, option: String) {
        _notificationSettings.update { current ->
            val preferences = current[groupID] ?: GroupNotificationPreferences()
            current + (groupID to preferences.copy(newPost = option))
        }
    }

    // chat enabled
    fun setChatEnabled(groupID: String, option: Boolean) {
        _chatEnabled.update { current ->
            current + (groupID to option)
        }
    }
}