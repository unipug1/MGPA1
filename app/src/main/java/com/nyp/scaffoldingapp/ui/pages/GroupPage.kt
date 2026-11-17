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

@Composable
fun GroupPage(
    modifier: Modifier = Modifier,
    viewModel: GroupViewModel
) {
    val groups by viewModel.groups.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    var showNotifications by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        // header
        Row(modifier = Modifier.fillMaxWidth()
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
        LazyColumn(modifier = Modifier.fillMaxWidth()
            .weight(1f)
        ) {
            items(groups, key = {it.groupID}) { item ->
                GroupRow(
                    group = item,
                )
            }
        }
    }

    if (showNotifications) {
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
                Column(modifier = Modifier.fillMaxWidth()
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
    group: Group
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
            ),
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

// Preview
@Preview(showBackground = true)
@Composable
private fun MockGroupsPagePreview() {
    MaterialTheme { GroupPage(Modifier, viewModel = GroupViewModel()) }
}