package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.foundation.clickable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.TextField
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Button
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect

@Composable
fun GroupPage(
    modifier: Modifier = Modifier,
    viewModel: GroupViewModel
) {
    val groups by viewModel.groups.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val messages by viewModel.messages.collectAsState()

    var selectedGroupID by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedGroup = groups.find {it.groupID == selectedGroupID }

    var showNotifications by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = selectedGroupID != null) {
        selectedGroupID = null
    }

    Column(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        if (selectedGroup != null) {
            GroupChatPage(
                group = selectedGroup,
                groupMessages = messages.filter {
                    it.groupID == selectedGroup.groupID
                },
                onBack = { selectedGroupID = null },
                onSendMessage = { messageText ->
                    viewModel.sendMessage(selectedGroup.groupID, messageText)
                }
            )
        }
        else {
            // header
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My groups",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(
                    onClick = { showNotifications = true }
                ) {
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔔")
                    }
                }
            }
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ) {
                items(groups, key = {it.groupID}) { item ->
                    GroupRow(
                        group = item,
                        onChatClicked = { groupID ->
                            showNotifications = false
                            selectedGroupID = groupID
                        }
                    )
                }
            }
        }
    }

    if (showNotifications && selectedGroup == null) {
        Popup(
            alignment = Alignment.TopEnd,
            onDismissRequest = {
                showNotifications = false
            },
            properties = PopupProperties(
                focusable = true
            )
        ) {
            Surface(
                modifier = Modifier.width(250.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notifications",
                            style = MaterialTheme.typography.titleLarge
                        )

                        TextButton(
                            onClick = { viewModel.clearNotifications() }
                        ) {
                            Text("Delete")
                        }
                    }
                    LazyColumn(modifier = Modifier.heightIn(max = 450.dp)
                    ) {
                        if (notifications.isEmpty()) {
                            item {
                                Text(
                                    text = "No notifications",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        else {
                            items(notifications, key = {it.notificationID}) { item ->
                                val group = groups.find { it.groupID == item.groupID }
                                val groupName = group?.groupName ?: "Unknown group"
                                NotificationRow(
                                    notification = item,
                                    groupName = groupName
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupRow(
    group: Group,
    onChatClicked: (String) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier
            .weight(1f)
        ) {
            Text(
                text = group.groupName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${group.yearOfStudy} · " +
                        "${group.memberCount}/${group.capacity} people · " +
                        group.ownerName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(modifier = Modifier
            .size(56.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onChatClicked(group.groupID)
            },
            contentAlignment = Alignment.Center
        ) {
            Text("Chat")
        }
    }
}

@Composable
private fun NotificationRow(
    notification: GroupNotification,
    groupName: String
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(groupName, style = MaterialTheme.typography.titleMedium)
        Text(notification.message, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = formatRelativeTime(notification.date),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GroupChatPage(
    group: Group,
    groupMessages: List<ChatMessage>,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    val sortedMessages = groupMessages.sortedBy { it.timestamp }
    var messageInput by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState() // keeps track of scrolling pos

    LaunchedEffect(group.groupID, groupMessages.size) {
        if (groupMessages.isNotEmpty()) {
            // go to last message
            listState.animateScrollToItem(groupMessages.lastIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("<")
            }

            Text(
                text = "Group chat",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(modifier= Modifier.align(Alignment.CenterEnd)
            ) {
                IconButton(onClick = {},
                ) {
                    Text("🔔")
                }
                IconButton(onClick = {},
                ) {
                    Text("ⓘ")
                }
            }
        }
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
            state = listState
        ) {
            items(sortedMessages.size,
                key = { index -> sortedMessages[index].messageID }
            ) { item ->
                val message = sortedMessages[item]

                val currentDate = formatMessageDate(message.timestamp)
                val previousDate = if (item > 0) {
                    formatMessageDate(sortedMessages[item -1].timestamp)
                }
                else {
                    null
                }

                if (currentDate != previousDate) {
                    Text(
                        text = currentDate,
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 12.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                MessageBubble(message = message)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // reserve space for plus button
            Spacer(modifier = Modifier.width(32.dp))

            TextField(
                value = messageInput,
                onValueChange = {messageInput = it},
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Message")
                },
                shape = RoundedCornerShape(24.dp)
            )

            // reserve space for emoji button
            Spacer(modifier = Modifier.width(32.dp))

            Button(
                onClick = {
                    if (messageInput.isNotBlank()) {
                        onSendMessage(messageInput)
                        messageInput = "" // clear
                    }
                },
                enabled = messageInput.isNotBlank()
            ) {
                Text("↑")
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = if (message.isMine) {
            Arrangement.End // right
        } else {
            Arrangement.Start // left
        }
    ) {
        Column(modifier = Modifier.widthIn(max = 250.dp)
        ) {
            if (!message.isMine) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = message.message,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Text(
                text = formatMessageTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(
                    if (message.isMine) {
                        Alignment.End // right
                    }
                    else {
                        Alignment.Start // left
                    }
                )
            )
        }
    }
}

private fun formatRelativeTime(timestamp: Long): String {
    val currentTime = System.currentTimeMillis()
    val difference = (currentTime - timestamp).coerceAtLeast(0L)

    val minutes = difference / (1000 * 60)
    val hours = difference / (1000 * 60 * 60)
    val days = difference / (1000 * 60 * 60 * 24)

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        else -> "${days}d ago"
    }
}
private fun formatMessageTime(timestamp: Long): String {
    val formatter = java.text.SimpleDateFormat(
        "h:mm:a",
        java.util.Locale.getDefault()
    )

    return formatter.format(java.util.Date(timestamp))
}
private fun formatMessageDate(timestamp: Long): String {
    val formatter = java.text.SimpleDateFormat(
        "EEE, MMM d,yyyy",
        java.util.Locale.getDefault()
    )

    return formatter.format(java.util.Date(timestamp))
}

// Preview
@Preview(showBackground = true)
@Composable
private fun MockGroupsPagePreview() {
    MaterialTheme { GroupPage(Modifier, viewModel = GroupViewModel()) }
}