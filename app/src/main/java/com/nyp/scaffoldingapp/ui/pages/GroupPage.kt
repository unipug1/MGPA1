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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.TextField
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Button
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableIntStateOf
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date
import java.time.LocalDate
import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import androidx.compose.material3.Switch
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton

@Composable
fun GroupPage(
    modifier: Modifier = Modifier,
    viewModel: GroupViewModel
) {
    val groups by viewModel.groups.collectAsState()
    val discoverGroups by viewModel.discoverGroups.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val studyRecords by viewModel.studyRecords.collectAsState()
    val groupChatEnabledByGroup by viewModel.groupChatEnabled.collectAsState()

    // opens group chat
    var selectedGroupID by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedGroup = groups.find {it.groupID == selectedGroupID }
    // open group home
    var selectedHomeGroupID by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedHomeGroup = groups.firstOrNull { it.groupID == selectedHomeGroupID }

    // test value - to be replaced with user's value
    var userYearOfStudy by rememberSaveable { mutableStateOf("Poly & ITE") }

    var showNotifications by rememberSaveable { mutableStateOf(false) }
    var showDiscoverGroups by rememberSaveable { mutableStateOf(false) }

    BackHandler(
        enabled = selectedGroupID != null ||
                selectedHomeGroupID != null ||
                showDiscoverGroups)
    {
        when {
            selectedHomeGroupID != null -> {
                selectedHomeGroupID = null
            }
            selectedGroupID != null -> {
                selectedGroupID = null
            }
            showDiscoverGroups -> {
                showDiscoverGroups = false
            }
        }
    }

    Column(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        if (selectedHomeGroup != null) {
            GroupHomePage(
                group = selectedHomeGroup,
                studyRecords = studyRecords,
                onBack = {
                    selectedHomeGroupID = null
                },
                groupChatEnabled = groupChatEnabledByGroup[selectedHomeGroup.groupID] ?: true,
                onGroupChatEnabledChange = { enabled ->
                    viewModel.updateGroupChatEnabled(
                        groupID = selectedHomeGroup.groupID,
                        enabled = enabled
                    )
                }
            )
        }
        else if (selectedGroup != null) {
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
        else if (showDiscoverGroups) {
            DiscoverGroupsPage(
                userYearOfStudy = userYearOfStudy,
                discoverGroups = discoverGroups,
                onBack = { showDiscoverGroups = false },
                onCreateGroup = { newGroup ->
                    viewModel.createGroup(newGroup)
                    showDiscoverGroups = false
                    selectedHomeGroupID = newGroup.groupID
                },
                onJoinGroup = { group ->
                    val joinedSuccessfully = viewModel.joinGroup(group)
                    if (joinedSuccessfully) {
                        showDiscoverGroups = false
                        selectedHomeGroupID = group.groupID
                    }
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
                        groupChatEnabled = groupChatEnabledByGroup[item.groupID] ?: true,
                        onGroupClicked = {
                            selectedHomeGroupID = item.groupID
                        },
                        onChatClicked = {
                            showNotifications = false
                            val isChatEnabled = groupChatEnabledByGroup[item.groupID] ?: true
                            if (isChatEnabled) {
                                selectedGroupID = item.groupID
                            }
                        }
                    )
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                FloatingActionButton(onClick = { showDiscoverGroups = true },
                    modifier = Modifier.align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Text("+")
                }
            }
        }
    }

    if (showNotifications && selectedGroup == null && !showDiscoverGroups) {
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
    groupChatEnabled: Boolean,
    onGroupClicked: (String) -> Unit,
    onChatClicked: (String) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onGroupClicked(group.groupID)
        }
        .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)
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
                if (groupChatEnabled) {
                    onChatClicked(group.groupID)
                }
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
                else { null }

                if (currentDate != previousDate) {
                    Text(
                        text = currentDate,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                MessageBubble(message = message)
            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
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

@Composable
private fun DiscoverGroupsPage(
    userYearOfStudy: String,
    discoverGroups: List<Group>,
    onBack: () -> Unit,
    onCreateGroup: (Group) -> Unit,
    onJoinGroup: (Group) -> Unit,
) {
    // 0 - new, 1 - study time
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var filterByYear by rememberSaveable { mutableStateOf(false) }
    var filterByAvailable by rememberSaveable { mutableStateOf(false) }
    var filterByPublic by rememberSaveable { mutableStateOf(false) }

    var showCreateGroup by rememberSaveable { mutableStateOf(false) }
    var selectedDiscoverGroupID by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedDiscoverGroup = discoverGroups.firstOrNull { it.groupID == selectedDiscoverGroupID }

    var showPasswordDialog by rememberSaveable { mutableStateOf(false) }

    if (showCreateGroup) {
        CreateGroupPage(
            onBack = { showCreateGroup = false },
            onCreateGroup = { newGroup ->
                onCreateGroup(newGroup)
                showCreateGroup = false
            }
        )
    }
    else {
        // box allows children to overlap
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Text("<")
                    }
                    Text(
                        text = "Study group",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                val tabs = listOf("New", "Study time")
                TabRow(
                    selectedTabIndex = selectedTab
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                            },
                            text = {
                                Text(title)
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = filterByYear,
                            onCheckedChange = {
                                filterByYear = it
                            }
                        )

                        Text(
                            text = userYearOfStudy,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = filterByAvailable,
                            onCheckedChange = {
                                filterByAvailable = it
                            }
                        )

                        Text(
                            text = "Available",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = filterByPublic,
                            onCheckedChange = {
                                filterByPublic = it
                            }
                        )

                        Text(
                            text = "Public",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                when (selectedTab) {
                    0 -> {
                        Text(
                            text = "Groups sorted by creation date",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    1 -> {
                        Text(
                            text = "Groups sorted by study time",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                val sortedGroups = when (selectedTab) {
                    0 -> discoverGroups.sortedByDescending { it.creationDate }
                    1 -> discoverGroups.sortedByDescending { it.totalStudyTime }
                    else -> discoverGroups
                }

                val filteredGroups = sortedGroups.filter { item ->
                    val matchesYear = !filterByYear ||
                            item.yearOfStudy == userYearOfStudy

                    val matchesAvailability = !filterByAvailable ||
                            item.memberCount < item.capacity

                    val matchesPrivacy = !filterByPublic ||
                            item.privacyStatus.equals("Public", ignoreCase = true)

                    matchesYear && matchesAvailability && matchesPrivacy
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (filteredGroups.isEmpty()) {
                        item {
                            Text(
                                text = "No groups found",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    items(filteredGroups, key = { it.groupID }) { item ->
                        DiscoverGroupCard(
                            group = item,
                            onClick = {
                                selectedDiscoverGroupID= item.groupID
                            }
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = { showCreateGroup = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text("+")
            }
        }

        if (selectedDiscoverGroup != null) {
            DiscoverGroupDetailsSheet(
                group = selectedDiscoverGroup,
                onDismiss = { selectedDiscoverGroupID = null },
                onJoinClicked = {
                    if (selectedDiscoverGroup.password.isNotBlank()) {
                        showPasswordDialog = true
                    } else {
                        onJoinGroup(selectedDiscoverGroup)
                        selectedDiscoverGroupID = null
                    }
                }
            )
        }
        if (showPasswordDialog && selectedDiscoverGroup != null) {
            PrivateGroupPasswordDialog(
                group = selectedDiscoverGroup,
                onDismiss = { showPasswordDialog = false },
                onConfirm = {
                    showPasswordDialog = false
                    onJoinGroup(selectedDiscoverGroup)
                    selectedDiscoverGroupID = null
                }
            )
        }
    }
}

@Composable
private fun DiscoverGroupCard(
    group: Group,
    onClick: () -> Unit
) {
    Card(onClick = onClick,
        modifier = Modifier.fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = group.groupName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${group.yearOfStudy} · " +
                "${group.memberCount}/${group.capacity} members",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Group leader: ${group.ownerName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Daily goal: ${formatStudyDuration(group.dailyGoalTime)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Total study time: ${formatStudyDuration(group.totalStudyTime)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = group.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Privacy: ${group.privacyStatus}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Created: ${formatGroupCreationDate(group.creationDate)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CreateGroupPage(
    onBack: () -> Unit,
    onCreateGroup: (Group) -> Unit
) {
    var groupName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var selectedDailyGoal by rememberSaveable { mutableIntStateOf(0) }
    var selectedCapacity by rememberSaveable { mutableIntStateOf(0) }
    var description by rememberSaveable { mutableStateOf("") }
    var groupNickname by rememberSaveable { mutableStateOf("") }
    var selectedJoinMethod by rememberSaveable { mutableStateOf("Join now") }

    // 1 - group details, 2 - join method, 3 - group profile
    var currentStep by rememberSaveable { mutableIntStateOf(1) }

    val categories = listOf(
        "Secondary", "JC Year 1", "JC Year 2", "Poly & ITE"
    )
    val dailyGoals = (1..10).toList()
    val capacity = (2..30).toList()

    Column(modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
            IconButton(onClick = {
                if (currentStep > 1) {
                    currentStep--
                } else {
                    onBack()
                }
            },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("<")
            }
            Text(
                text = if (currentStep > 1) {
                    ""
                } else {
                    "Create group"
                },
                modifier = Modifier.align(Alignment.Center)
            )
        }

        when (currentStep) {
            1 -> {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column {
                            TextField(
                                value = groupName,
                                onValueChange = { groupName = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text("Enter group name")
                                },
                                singleLine = true
                            )
                        }
                    }

                    Surface(modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column{
                            CreateGroupDropdownRow(
                                label = "Category",
                                selectedValue = selectedCategory.ifEmpty {
                                    "Select a category"
                                },
                                options = categories,
                                onValueSelected = { selectedCategory = it }
                            )
                            HorizontalDivider(modifier = Modifier.padding(16.dp))
                            CreateGroupDropdownRow(
                                label = "Daily goal time",
                                selectedValue = if (selectedDailyGoal == 0) {
                                    "Daily goal time"
                                } else {
                                    "$selectedDailyGoal hours"
                                },
                                options = dailyGoals.map { "$it hours per day" },
                                onValueSelected = { selected ->
                                    // substring extracts numeric parts of values
                                    selectedDailyGoal = selected.substringBefore(" ").toInt()
                                }
                            )
                            HorizontalDivider(modifier = Modifier.padding(16.dp))
                            CreateGroupDropdownRow(
                                label = "Capacity",
                                selectedValue = "$selectedCapacity people",
                                options = capacity.map { "$it people" },
                                onValueSelected = { selected ->
                                    selectedCapacity = selected.substringBefore(" ").toInt()
                                }
                            )
                        }
                    }

                    Surface(modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        TextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp),
                            placeholder = {
                                Text("Description")
                            }
                        )
                    }
                }
                // next button
                Button(
                    onClick = {
                        currentStep = 2
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    enabled = groupName.isNotBlank() &&
                            selectedCategory.isNotBlank() &&
                            selectedDailyGoal > 0 &&
                            selectedCapacity > 0
                ) {
                    Text(
                        text = "Next 1/3",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            2 -> {
                Column(modifier = Modifier.fillMaxWidth()
                    .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Select a join method",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(modifier = Modifier.clickable {
                                    selectedJoinMethod = "Join now"
                                }
                                    .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "Join now",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "Anyone can join",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    RadioButton(
                                        selected = selectedJoinMethod == "Join now",
                                        onClick = {
                                            selectedJoinMethod = "Join now"
                                        }
                                    )
                                }
                            }
                            Card(modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(modifier = Modifier.clickable {
                                    selectedJoinMethod = "Join with password"
                                }
                                    .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "Join with password",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "Members must enter the password to join",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    RadioButton(
                                        selected = selectedJoinMethod == "Join with password",
                                        onClick = {
                                            selectedJoinMethod = "Join with password"
                                        }
                                    )
                                }
                                if (selectedJoinMethod == "Join with password") {
                                    HorizontalDivider()

                                    TextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        placeholder = {
                                            Text("Enter group password")
                                        },
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        currentStep = 3
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    enabled = selectedJoinMethod == "Join now" || password.isNotBlank()
                ) {
                    Text("Next 2/3")
                }
            }
            3 -> {
                Column(modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Group Profile Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Surface(modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        TextField(
                            value = groupNickname,
                            onValueChange = { groupNickname = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                // take from username
                                Text("Enter group nickname")
                            },
                            singleLine = true
                        )
                    }

                    Text(
                        text = "Study icon",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // placeholder box for icon
                    Surface(modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center
                        ) {
                            Text("Person")
                        }
                    }
                }
                Button(
                    onClick = {
                        val newGroup = Group(
                            groupID = java.util.UUID.randomUUID().toString(),
                            groupName = groupName.trim(),
                            yearOfStudy = selectedCategory,
                            memberCount = 1,
                            capacity = selectedCapacity,
                            ownerName = groupNickname.trim(),
                            privacyStatus = if (selectedJoinMethod == "Join with password") {
                                "Private"
                            } else {
                                "Public"
                            },
                            dailyGoalTime = selectedDailyGoal * 3600L,
                            description = description.trim(),
                            joinMethod = selectedJoinMethod,
                            password = if (selectedJoinMethod == "Join with password") {
                                password.trim()
                            } else {
                                ""
                            }
                        )
                        onCreateGroup(newGroup)
                    },
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    enabled = groupNickname.isNotBlank()
                ) {
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateGroupDropdownRow(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))

            Box {
                Row(verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedValue,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = " >",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onValueSelected(option)
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
private fun GroupHomePage(
    group: Group,
    studyRecords: List<GroupStudyRecord>,
    onBack: () -> Unit,
    groupChatEnabled: Boolean,
    onGroupChatEnabledChange: (Boolean) -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf("Home") }
    var showGroupSettings by rememberSaveable { mutableStateOf(false) }
    var isGroupLeader by rememberSaveable { mutableStateOf(true) }
    var showMemberDetails by rememberSaveable { mutableStateOf(false) }

    if (showGroupSettings) {
        GroupSettingsPage(
            group = group,
            isGroupLeader = isGroupLeader,
            groupChatEnabled = groupChatEnabled,
            onGroupChatEnabledChange = onGroupChatEnabledChange,
            onBack = { showGroupSettings = false }
        )
    }
    else {
        Column(modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
            ) {
                IconButton(onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text("<")
                }
                Text(
                    text = group.groupName,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = {
                    showGroupSettings = true
                },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text("⚙")
                }
            }
            Column(modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    "Home" -> {
                        Surface(modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Group description",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Text(
                                    text = group.description.ifBlank {
                                        "No group description."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Column(modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Studying ",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${group.memberCount} members",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            LazyVerticalGrid(columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxWidth()
                                    .heightIn(max = 300.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(group.memberCount) {
                                    Surface(modifier = Modifier.fillMaxWidth()
                                            .aspectRatio(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Box(contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .clickable { showMemberDetails = true }
                                        ) {
                                            Text("Person")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "Rankings" -> {
                        GroupRankingsPage(
                            group = group,
                            studyRecords = studyRecords
                        )
                    }
                    "Invite" -> {
                        GroupInvitePage(
                            group = group
                        )
                    }
                }
            }
            GroupHomeNavigation(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                }
            )
        }
    }
    if (showMemberDetails && !showGroupSettings) {
        GroupMemberDetailsSheet(
            onDismiss = { showMemberDetails = false }
        )
    }
}

@Composable
private fun GroupHomeNavigation(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar {
        listOf(
            "Home", "Rankings", "Invite"
        ).forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = {
                    onTabSelected(tab)
                },
                icon = {

                },
                label = {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
        }
    }
}

@Composable
private fun GroupRankingsPage(
    group: Group,
    studyRecords: List<GroupStudyRecord>
) {
    var selectedPeriod by rememberSaveable { mutableStateOf("Daily") }
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now().toString())}
    var expanded by rememberSaveable { mutableStateOf(false) }

    val date = LocalDate.parse(selectedDate)
    val periodStart = when (selectedPeriod) {
        "Weekly" -> date.with(
            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        )
        "Monthly" -> date.withDayOfMonth(1)
        else -> date
    }
    val periodEnd = when (selectedPeriod) {
        "Weekly" -> periodStart.plusDays(6)
        "Monthly" -> periodStart.plusMonths(1).minusDays(1)
        else -> date
    }
    val filteredRecords = studyRecords.filter { record ->
        val recordDate = LocalDate.parse(record.date)
        record.groupID == group.groupID &&
                !recordDate.isBefore(periodStart) &&
                !recordDate.isAfter(periodEnd)
    }
    val rankedRecords = filteredRecords
        .groupBy { it.memberID }
        .map { (_, studyRecords ) ->
            val member = studyRecords.first()

            member.copy(
                studyTimeSeconds = studyRecords.sumOf { it.studyTimeSeconds }
            )
        }.sortedByDescending { it.studyTimeSeconds }
    val totalStudyTime = rankedRecords.sumOf { it.studyTimeSeconds }
    val averageStudyTime = if (rankedRecords.isNotEmpty()) {
        totalStudyTime / rankedRecords.size
    } else { 0L }

    Column(modifier = Modifier.fillMaxSize()
        .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Rankings",
            style = MaterialTheme.typography.titleLarge
        )
        Surface(modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    selectedDate = when (selectedPeriod) {
                        "Weekly" -> date.minusWeeks(1)
                        "Monthly" -> date.minusMonths(1)
                        else -> date.minusDays(1)
                    }.toString()
                }) {
                    Text("<")
                }
                Text(
                    text = when (selectedPeriod) {
                        "Weekly" -> {
                            val start = date.with(
                                TemporalAdjusters.previousOrSame(
                                    DayOfWeek.MONDAY
                                )
                            )
                            val end = start.plusDays(6)
                            "${start.format(DateTimeFormatter.ofPattern("MMM d"))} - " +
                                    end.format(DateTimeFormatter.ofPattern("MMM d"))
                        }

                        "Monthly" -> {
                            date.format(DateTimeFormatter.ofPattern("MMM yyyy"))
                        }
                        else -> {
                            date.format(DateTimeFormatter.ofPattern("MMM d"))
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                IconButton(onClick = {
                    selectedDate = when (selectedPeriod) {
                        "Weekly" -> date.plusWeeks(1)
                        "Monthly" -> date.plusMonths(1)
                        else -> date.plusDays(1)
                    }.toString()
                }) {
                    Text(">")
                }
                Spacer(modifier = Modifier.weight(1f))
                Box {
                    Row(modifier = Modifier
                        .width(100.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { expanded = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = selectedPeriod,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "v",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(expanded = expanded,
                        onDismissRequest = { expanded = false}
                    ) {
                        listOf("Daily", "Weekly", "Monthly").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedPeriod = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Total time ${formatStudyTime(totalStudyTime)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Average time ${formatStudyTime(averageStudyTime)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My rank ",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${group.dailyGoalTime / 3600}h",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        LazyColumn {
            if (rankedRecords.isEmpty()) {
                item {
                    Text(
                        text = "No study records for this period",
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else {
                itemsIndexed(rankedRecords) { index, record ->
                    GroupRankingRow(
                        rank = index + 1,
                        nickname = record.nickname,
                        yearOfStudy = record.yearOfStudy,
                        studyTimeSeconds = record.studyTimeSeconds,
                        dailyGoalSeconds = record.dailyGoalSeconds
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupRankingRow(
    rank: Int,
    nickname: String,
    yearOfStudy: String,
    studyTimeSeconds: Long,
    dailyGoalSeconds: Long
) {
    val progress = if (dailyGoalSeconds > 0L) {
        (studyTimeSeconds.toFloat() / dailyGoalSeconds).coerceIn(0f, 1f)
    } else { 0F }

    Row(modifier = Modifier.fillMaxWidth()
        .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // rank badge
        Surface(modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        // member details
        Column(modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = yearOfStudy,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = nickname,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = String.format(
                        Locale.US,
                        "%d:%02d:%02d",
                        studyTimeSeconds / 3600,
                        (studyTimeSeconds % 3600) / 60,
                        studyTimeSeconds % 60
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GroupInvitePage(
    group: Group
) {
    Column(modifier = Modifier.fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Invite friends",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Surface(modifier = Modifier.size(200.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "QR Code",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = "Invitation link",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = {

                },
                modifier = Modifier.weight(1f),
                enabled = false
            ) {
                Text(
                    text = "Copy link",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            OutlinedButton(onClick = {
                },
                modifier = Modifier.weight(1f),
                enabled = false
            ) {
                Text(
                    text = "Share link",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Text(
            text = "Share this link with friends or groups you want to study with",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "They'll automatically join the group when they open the link",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GroupSettingsPage(
    group: Group,
    isGroupLeader: Boolean,
    groupChatEnabled: Boolean,
    onGroupChatEnabledChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    var showChatSettings by rememberSaveable { mutableStateOf(false) }

    if (showChatSettings) {
        GroupChatSettingsPage(
            isGroupLeader = isGroupLeader,
            groupChatEnabled = groupChatEnabled,
            onGroupChatEnabledChange = onGroupChatEnabledChange,
            onBack = { showChatSettings = false }
        )
    }
    else {
        Column(modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            ) {
                IconButton(onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text("<")
                }
                Text(
                    text = "Group settings",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Column(modifier = Modifier.weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Personal settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Card(modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column{
                            GroupSettingsRow(
                                title = "Group profile",
                                onClick = {
                                    // step 3/3 from the creation of groups
                                }
                            )
                            HorizontalDivider()
                            GroupSettingsRow(
                                title = "Group notification settings",
                                onClick = {

                                }
                            )
                        }
                    }
                }
                if (isGroupLeader) {
                    Column(modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Group leader menu",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Card(modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column{
                                GroupSettingsRow(
                                    title = "Change group name",
                                    onClick = {}
                                )
                                HorizontalDivider()
                                GroupSettingsRow(
                                    title = "Group description",
                                    value = group.description,
                                    onClick = {}
                                )
                                HorizontalDivider()
                                GroupSettingsRow(
                                    title = "Change category",
                                    value = group.yearOfStudy,
                                    onClick = {}
                                )
                                HorizontalDivider()
                                GroupSettingsRow(
                                    title = "Change daily goal",
                                    value = "${group.dailyGoalTime / 3600}h",
                                    onClick = {}
                                )
                                HorizontalDivider()
                                GroupSettingsRow(
                                    title = "Change capacity",
                                    value = "${group.capacity} people",
                                    onClick = {}
                                )
                                HorizontalDivider()
                                GroupSettingsRow(
                                    title = "Change password",
                                    value = if (group.privacyStatus.equals("Public", ignoreCase = true)) {
                                        "Public" } else { "Private" },
                                    onClick = {}
                                )
                            }
                        }
                        Card(modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column{
                                GroupSettingsRow(
                                    title = "Manage group members",
                                    onClick = {}
                                )
                                HorizontalDivider()
                                GroupSettingsRow(
                                    title = "Nudge everyone at once",
                                    onClick = {}
                                )
                                HorizontalDivider()
                                GroupSettingsRow(
                                    title = "Group chat",
                                    value = if (groupChatEnabled) "On" else "Off",
                                    onClick = {
                                        showChatSettings = true
                                    }
                                )
                                HorizontalDivider()
                                GroupSettingsRow(
                                    title = "Show added time",
                                    value = "On",
                                    onClick = {}
                                )
                                HorizontalDivider()
                                GroupSettingsRow(
                                    title = "Promote group",
                                    value = "41s ago",
                                    onClick = {}
                                )
                            }
                        }
                        Card(modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            GroupSettingsRow(
                                title = "Disband group",
                                onClick = {}
                            )
                        }
                    }
                }
                if (!isGroupLeader) {
                    Column(modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Group info",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Card(modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column{
                                GroupSettingsRow(
                                    title = "Group description",
                                    value = group.description,
                                    onClick = {}
                                )
                                HorizontalDivider()
                                GroupSettingsRow(
                                    title = "Group leader",
                                    value = group.ownerName,
                                    onClick = {}
                                )
                            }
                        }
                        Card(modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            GroupSettingsRow(
                                title = "Category",
                                value = group.yearOfStudy,
                                onClick = {}
                            )
                            HorizontalDivider()
                            GroupSettingsRow(
                                title = "Daily goal",
                                value = "${group.dailyGoalTime / 3600}h",
                                onClick = {}
                            )
                            HorizontalDivider()
                            GroupSettingsRow(
                                title = "Capacity",
                                value = "${group.capacity} people",
                                onClick = {}
                            )
                        }
                        Card(modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            GroupSettingsRow(
                                title = "Leave group",
                                onClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupSettingsRow(
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = ">"
        )
    }
}

@Composable
private fun GroupChatSettingsPage(
    isGroupLeader: Boolean,
    groupChatEnabled: Boolean,
    onGroupChatEnabledChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
        ) {
            IconButton(onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("<")
            }
            Text(
                text = "Group chat settings",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Column(modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Group chat",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Card(modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Group chat",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = groupChatEnabled,
                        onCheckedChange = onGroupChatEnabledChange,
                        enabled = isGroupLeader
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupMemberDetailsSheet(
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // placeholder for study icon
                Box(modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Icon",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(onClick = {}
                ) {
                    Text(
                        text = "Nudge",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = {}
                ) {
                    Text(
                        text = "Statistics",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                IconButton(onClick = {}) {
                    Text("⋮")
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "sze:)",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Card(modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "0:09:48",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column (modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Start",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "PM 9.58",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Column(modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "End",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "PM 11:29",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Max focus",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "0:07:38",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Column(modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Allowed Apps",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "0:00:00",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
            Text(
                text = "JC Year 2 · Nov 16, 2026 joined",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiscoverGroupDetailsSheet(
    group: Group,
    onDismiss: () -> Unit,
    onJoinClicked: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = group.groupName,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "${group.yearOfStudy} · ${formatGroupCreationDate(group.creationDate)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Goal ${formatStudyDuration(group.dailyGoalTime)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Members ${group.memberCount}/${group.capacity} people",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Group leader ${group.ownerName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Study time ${formatStudyDuration(group.totalStudyTime)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            HorizontalDivider()
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Group description",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = group.description.ifBlank {
                        "No group description available."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Close")
                }
                Button(onClick = onJoinClicked,
                    modifier = Modifier.weight(1.5f)
                ) {
                    Text(
                        text = "Join",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivateGroupPasswordDialog(
    group: Group,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var enteredPassword by rememberSaveable { mutableStateOf("") }
    var isPasswordIncorrect by rememberSaveable { mutableStateOf(false) }

    AlertDialog(onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Private group",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = enteredPassword,
                    onValueChange = {
                        enteredPassword = it
                        isPasswordIncorrect = false
                    },
                    placeholder = { Text("Enter password") },
                    singleLine = true,
                    isError = isPasswordIncorrect,
                    supportingText = {
                        if (isPasswordIncorrect) {
                            Text(
                                text = "Incorrect password. Please try again.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (enteredPassword == group.password) {
                        onConfirm(enteredPassword)
                    } else {
                        isPasswordIncorrect = true
                    }
                },
                enabled = enteredPassword.isNotBlank()
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
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
    val formatter = SimpleDateFormat(
        "h:mm:a",
        Locale.getDefault()
    )

    return formatter.format(Date(timestamp))
}
private fun formatMessageDate(timestamp: Long): String {
    val formatter = SimpleDateFormat(
        "EEE, MMM d,yyyy",
        Locale.getDefault()
    )

    return formatter.format(Date(timestamp))
}
private fun formatStudyDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return "${hours}h ${minutes}m"
}
private fun formatStudyTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return String.format(
        Locale.US,
        "%d:%02d:%02d",
        hours, minutes, remainingSeconds
    )
}
private fun formatGroupCreationDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    )

    return dateFormat.format(Date(timestamp))
}

// Preview
@Preview(showBackground = true)
@Composable
private fun MockGroupsPagePreview() {
    MaterialTheme { GroupPage(Modifier, viewModel = GroupViewModel()) }
}