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
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.layout.Remeasurement
import androidx.compose.ui.res.stringArrayResource
import kotlinx.coroutines.selects.select


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

    Surface(Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background) {

        if (selectedGroup != null) {
            // look up messages
            val groupMessages = messagesByGroup[selectedGroup.groupID].orEmpty()

            GroupChatPage(group = selectedGroup,
                onBack = { selectedGroupID = null },
                groupChatMessages = groupMessages,
                onSend = {messageText ->
                    viewModel.sendMessage(selectedGroup.groupID, messageText)
                }
            )
        }
        else {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text("My groups")
                    Spacer(Modifier.weight(1f))
                    Button(onClick = {println("Notifications")}) {
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

                Button(onClick = {println("Add")}) {
                    Text("+")
                }
            }
        }
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
                          onBack: () -> Unit,
                          onSend: (String) -> Unit) {

    var userInput by rememberSaveable {
        mutableStateOf("")
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = onBack) {
                Text("Back")
            }
            Text(group.groupName)
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
                enabled = userInput.isNotBlank()) {
                Text("↑")
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