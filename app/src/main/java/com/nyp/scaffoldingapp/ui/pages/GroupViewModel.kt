package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupViewModel : ViewModel() {
    // key is a group id, value is group list of messages
    private val _messagesByGroup = MutableStateFlow<Map<String, List<ChatMessage>>>(
        mapOf(
            "1" to listOf(ChatMessage("1", "tester", "hello")),
            "2" to listOf(ChatMessage("2", "tester", "testing"))
        )
    )

    val messagesByGroup = _messagesByGroup.asStateFlow()

    // send message
    fun sendMessage(groupID: String, text: String) {
        val trimmedText = text.trim()
        if (trimmedText.isEmpty()) return
        // create message w unique id
        val newMessage = ChatMessage("3", "Me", trimmedText)
        // append message + return updated map
        _messagesByGroup.update { current ->
            val updatedMessages = current[groupID].orEmpty() + newMessage
            current + (groupID to updatedMessages)
        }
    }
}