package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.core.graphics.toColorInt
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.layout.Remeasurement
import androidx.compose.ui.res.stringArrayResource
import kotlinx.coroutines.selects.select
import kotlin.math.exp

@Composable
fun GroupsPage(modifier: Modifier = Modifier, viewModel: GroupViewModel) {
    var groups by rememberSaveable {
        mutableStateOf(listOf(
            Group("testGroup", "1", "JC Year 2", 3600, 3, 2, "test", "01-01-2022", "tester", "public"),
            Group("testGroup2", "2", "Poly", 3600, 3, 2, "test", "01-01-2022", "tester", "private")
        ))
    }

    var selectedGroupID by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val selectedGroup = groups.firstOrNull{it.groupID == selectedGroupID}

    val messagesByGroup by viewModel.messagesByGroup.collectAsState()
    val notificationSettings by viewModel.notificationSettings.collectAsState()
    val chatEnabledSetting by viewModel.chatEnabled.collectAsState()

    var showNotifications by rememberSaveable {
        mutableStateOf(false)
    }

    var showDiscover by rememberSaveable {
        mutableStateOf(false)
    }

    Surface(Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background) {

        if (selectedGroup != null) {
            // look up messages
            val groupMessages = messagesByGroup[selectedGroup.groupID].orEmpty()
            val notifSettings = notificationSettings[selectedGroup.groupID]?.groupChat ?: "On"
            val noticeSettings = notificationSettings[selectedGroup.groupID]?.notice ?: "On"
            val commentSettings = notificationSettings[selectedGroup.groupID]?.comment ?: "On"
            val newPostSettings = notificationSettings[selectedGroup.groupID]?.newPost ?: "On"
            val isChatEnabled = chatEnabledSetting[selectedGroup.groupID] ?: true


            GroupChatPage(group = selectedGroup,
                onBack = { selectedGroupID = null },
                groupChatMessages = groupMessages,
                onSend = {messageText ->
                    viewModel.sendMessage(selectedGroup.groupID, messageText)
                },
                selectedOption = notifSettings,
                noticeOption = noticeSettings,
                commentOption = commentSettings,
                newPostOption = newPostSettings,
                chatEnabled = isChatEnabled,
                onOptionSelected = { option ->
                    viewModel.setNotificationSettings(selectedGroup.groupID, option)
                },
                onNoticeOptionSelected = { option ->
                    viewModel.setNoticeSettings(selectedGroup.groupID, option)
                },
                onCommentOptionSelected = { option ->
                    viewModel.setCommentSettings(selectedGroup.groupID, option)
                },
                onNewPostOptionSelected = { option ->
                    viewModel.setNewPostSettings(selectedGroup.groupID, option)
                },
                onChatEnabled = { enabled ->
                    viewModel.setChatEnabled(selectedGroup.groupID, enabled)
                }
            )
        }
        else if (showDiscover) {
            GroupsDiscoverPage(Modifier,
                onBack = {showDiscover = false})
        }
        else {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text("My groups")
                    Spacer(Modifier.weight(1f))
                    Button(onClick = {showNotifications = true}) {
                        Text("Bell")
                    }
                }


                // render group chat rows in scrolling list
                LazyColumn(Modifier.weight(1f)) {
                    items(groups, key = {it.groupID}) { item ->
                        GroupRow(item) { groupID ->
                            selectedGroupID = groupID
                        }
                    }
                }


                Button(onClick = {showDiscover = true}) {
                    Text("+")
                }
            }
        }
    }


    var groupNotifications by rememberSaveable {
        mutableStateOf(listOf(
            GroupNotification("1", "testGroup", "Aisha nudged you", "2 days ago"),
            GroupNotification("2", "testGroup2", "John nudged you", "4/24/2026")
        ))
    }


    if (showNotifications) {
        GroupNotificationsDialog(
            groupNotifications,
            onDismiss = {showNotifications = false},
            onDeleteAll = {groupNotifications = emptyList()}
        )
    }
}


@Composable
private fun GroupRow(group: Group,
                     onChatClick: (String) -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Text(group.groupName)


            Text("${group.yearOfStudy} · ${group.currentMemberCount}/${group.capacity} · ${group.ownerName}")
        }
        Button(onClick = {onChatClick(group.groupID)}) {
            // placeholder for now
            Text("Chat")
        }
    }
}


@Composable
private fun GroupChatPage(group: Group,
                          groupChatMessages: List<ChatMessage>,
                          selectedOption: String,
                          noticeOption: String,
                          commentOption: String,
                          newPostOption: String,
                          chatEnabled: Boolean,
                          onBack: () -> Unit,
                          onSend: (String) -> Unit,
                          onOptionSelected: (String) -> Unit,
                          onNoticeOptionSelected: (String) -> Unit,
                          onCommentOptionSelected: (String) -> Unit,
                          onNewPostOptionSelected: (String) -> Unit,
                          onChatEnabled: (Boolean) -> Unit
) {


    var userInput by rememberSaveable {
        mutableStateOf("")
    }
    var showNotificationSettings by rememberSaveable {
        mutableStateOf(false)
    }
    var showChatInfo by rememberSaveable {
        mutableStateOf(false)
    }


    if (showNotificationSettings) {
        GroupNotificationSettingsPage(
            onBack = {showNotificationSettings = false},
            selectedOption = selectedOption,
            noticeOption = noticeOption,
            commentOption = commentOption,
            newPostOption = newPostOption,
            onOptionSelected = onOptionSelected,
            onNoticeOptionSelected = onNoticeOptionSelected,
            onCommentOptionSelected = onCommentOptionSelected,
            onNewPostOptionSelected = onNewPostOptionSelected
        )
    }
    else if (showChatInfo) {
        GroupInfoPage(
            onBack = {showChatInfo = false},
            chatEnabled = chatEnabled,
            onChatEnabled = onChatEnabled
        )
    }
    else {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth()) {
                Button(onClick = onBack) {
                    Text("<")
                }
                Text(group.groupName)
                Spacer(Modifier.weight(1f))
                Button(onClick = {showNotificationSettings = true}) {
                    Text("Bell")
                }
                Button(onClick = {showChatInfo = true}) {
                    Text("Info")
                }
            }


            // render messages below - change UI later
            LazyColumn(Modifier.weight(1f)) {
                items(groupChatMessages, key = {it.id}) { chat ->
                    Text("${chat.messageText} by ${chat.senderName}")
                }
            }


            // user inputs
            Row() {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = {userInput = it},
                    modifier = Modifier.weight(1f),
                    placeholder = {Text("Message...")},
                )


                // send button
                Button(onClick = {onSend(userInput)
                    userInput = "" },
                    enabled = userInput.isNotBlank() && chatEnabled) {
                    Text("↑")
                }
            }
        }
    }
}


@Composable
private fun GroupNotificationSettingsPage(
    onBack: () -> Unit,
    selectedOption: String,
    noticeOption: String,
    commentOption: String,
    newPostOption: String,
    onOptionSelected: (String) -> Unit,
    onNoticeOptionSelected: (String) -> Unit,
    onCommentOptionSelected: (String) -> Unit,
    onNewPostOptionSelected: (String) -> Unit
)
{
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }


    val options = listOf("On", "Off while studying", "Off")


    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = onBack) {
                Text("<")
            }
            Text("Group notification settings")
        }


        Row(Modifier.fillMaxWidth()) {
            Text("Group chat")
            Box(
            ) {
                TextButton(onClick = { expanded = true }) {
                    Text(selectedOption)
                }


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onOptionSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }


        Row(Modifier.fillMaxWidth()) {
            Text("Notice")
            Box(
            ) {
                TextButton(onClick = { expanded = true }) {
                    Text(noticeOption)
                }


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onNoticeOptionSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }


        Row(Modifier.fillMaxWidth()) {
            Text("Comment")
            Box(
            ) {
                TextButton(onClick = { expanded = true }) {
                    Text(commentOption)
                }


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onCommentOptionSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }


        Row(Modifier.fillMaxWidth()) {
            Text("New post")
            Box(
            ) {
                TextButton(onClick = { expanded = true }) {
                    Text(newPostOption)
                }


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onNewPostOptionSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun GroupInfoPage(
    onBack: () -> Unit,
    chatEnabled: Boolean,
    onChatEnabled: (Boolean) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = onBack) {
                Text("<")
            }
            Text("Group Information")
        }


        Row(Modifier.fillMaxWidth()) {
            Text("Chat Enabled")
            Switch(
                checked = chatEnabled,
                onCheckedChange = onChatEnabled
            )
        }
    }
}


@Composable
private fun GroupNotificationsDialog(
    notifications: List<GroupNotification>,
    onDismiss: () -> Unit,
    onDeleteAll: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(Modifier.fillMaxHeight()
            .fillMaxWidth(0.5f),
        ) {
            Column(Modifier.padding(16.dp)) {
                // header
                Row(Modifier.fillMaxWidth()) {
                    Text("Notifications")
                    Spacer(Modifier.weight(1f))
                    Button(onClick = onDeleteAll,
                        enabled = notifications.isNotEmpty()) {
                        Text("Delete")
                    }
                }


                // list of notifications
                LazyColumn(Modifier.weight(1f)) {
                    items(notifications, key = {it.id}) { item ->
                        Column() {
                            Text(item.groupName, style = MaterialTheme.typography.titleSmall)
                            Text(item.message)
                            Text(item.timeLabel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupsDiscoverPage(modifier: Modifier = Modifier,
                               onBack: () -> Unit) {
    var selectedFilter by rememberSaveable {mutableStateOf("New")}
    val filters = listOf("New", "Attendance", "Study group")
    var yearChecked by rememberSaveable {mutableStateOf(false)}
    var availableChecked by rememberSaveable {mutableStateOf(false)}
    var publicChecked by rememberSaveable {mutableStateOf(false)}

    // test groups
    val groups = listOf(
        Group("lock in", "4", "JC Year 1", 3600, 3, 2, "test", "01-01-2022", "tester", "public"),
        Group("testGroup3", "5", "Secondary", 3600, 5, 5, "test", "01-01-2022", "tester", "private")
    )

    Surface(Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background) {
        Column(Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            // top bar: back button + title + search button
            Row(Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Button(onClick = onBack) {
                    Text("<")
                }
                Text("Study group", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = {println("Search button clicked")}) {
                    Text("Search")
                }
            }

            Spacer(Modifier.height(8.dp))

            // filters row
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(filter)
                        }
                    )
                }
            }

            HorizontalDivider(thickness = 5.dp)

            // dynamic sorting
            Text(
                text = when (selectedFilter) {
                    "New" -> "*Groups are sorted by creation date"
                    "Attendance" -> "*The group list is ordered by member's average attendance rate over the past week, with larger groups ranking higher."
                    else -> "*The group list is ordered by the average study time per member over the past week, with larger groups ranking higher."
                },
                style = MaterialTheme.typography.bodySmall
            )


            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // year
                Checkbox(
                    checked = yearChecked,
                    onCheckedChange = {yearChecked = it}
                )
                Text("J2")


                // available
                Checkbox(
                    checked = availableChecked,
                    onCheckedChange = {availableChecked = it}
                )
                Text("Available")


                // public
                Checkbox(
                    checked = publicChecked,
                    onCheckedChange = {publicChecked = it}
                )
                Text("Public")
            }

            val visibleGroups = if (availableChecked) {
                groups.filter { group -> group.currentMemberCount < group.capacity }
            } else {
                groups
            }

            // sorted output - lazy column
            LazyColumn(Modifier.weight(1f)) {
                items(visibleGroups, key = {it.groupID}) { item ->
                    Card(Modifier.padding(16.dp)) {
                        Column(Modifier.fillMaxWidth()) {
                            Text(item.yearOfStudy)
                            Text(item.groupName)
                            Row() {
                                Text("Goal ${item.dailyGoalTime}")
                                Text("Members ${item.currentMemberCount}/${item.capacity} people")
                                Text("Group leader ${item.ownerName}")
                            }
                            Row() {
                                Text("Attendance")
                                Text("Study time")
                                Text("Started")
                            }
                            HorizontalDivider(thickness = 2.dp)
                            Text(item.description)
                        }
                    }
                }
            }

            // create group button
            Box(Modifier
                .background(color = MaterialTheme.colorScheme.onSurfaceVariant, shape = CircleShape)) {
                FloatingActionButton(onClick = {println("Create group")}) {
                    Text("+")
                }
            }
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
private fun MockGroupsPagePreview() {
    MaterialTheme { GroupsPage(Modifier, GroupViewModel()) }
}