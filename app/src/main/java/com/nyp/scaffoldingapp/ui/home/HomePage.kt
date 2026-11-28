package com.nyp.scaffoldingapp.ui.home

import androidx.compose.foundation.background
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
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.key
import androidx.compose.ui.unit.Dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val YptInk = Color(0xFF222222)
private val YptPanel = Color(0xFFF5F5F5)
private val YptEditor = Color(0xFFF1F0F5)
private val YptMuted = Color(0xFF666666)

private fun clockText(seconds: Long): String =
    String.format(
        Locale.getDefault(),
        "%d:%02d:%02d",
        seconds / 3_600,
        seconds % 3_600 / 60,
        seconds % 60
    )

private fun restText(seconds: Long): String = when {
    seconds < 60 -> "${seconds}s"
    seconds < 3_600 -> "${seconds / 60}m ${seconds % 60}s"
    else -> "${seconds / 3_600}h ${seconds % 3_600 / 60}m ${seconds % 60}s"
}

private fun subjectColor(hex: String): Color {
    return try {
        Color(hex.toColorInt())
    } catch (_: IllegalArgumentException) {
        Color.Gray
    }
}

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onOpenBooks: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditor by rememberSaveable { mutableStateOf(false) }
    var showAdd by rememberSaveable { mutableStateOf(false) }
    var editId by rememberSaveable { mutableStateOf<String?>(null) }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White,
        contentColor = YptInk
    ) {
        when (val state = uiState) {
            HomeUIState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is HomeUIState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }

            is HomeUIState.Success -> {
                val activeSubject = state.activeSubject

                when {
                    activeSubject != null -> {
                        StudyScreen(
                            state = state,
                            subject = activeSubject,
                            onPause = viewModel::stopForBreak,
                            onOpenBooks = onOpenBooks
                        )

                        state.pendingBreakSeconds?.let { seconds ->
                            BreakLogDialog(
                                seconds = seconds,
                                onTagSelected = { tag -> viewModel.dismissBreakLog(tag) },
                                onNoLog = { viewModel.dismissBreakLog() }
                            )
                        }
                    }

                    showEditor -> {
                        BackHandler { showEditor = false }

                        EditSubjectsScreen(
                            subjects = state.subjects,
                            onBack = { showEditor = false },
                            onAdd = { showAdd = true },
                            onEdit = { editId = it.id },
                            onDelete = { deleteId = it.id },
                            onArchive = {
                                viewModel.setSubjectArchived(it.id, !it.isArchived)
                            }
                        )
                    }

                    else -> TimerHome(
                        state = state,
                        onStart = viewModel::startTimer,
                        onOpenEditor = { showEditor = true },
                        onOpenBooks = onOpenBooks,
                        onEdit = { editId = it.id },
                        onDelete = { deleteId = it.id },
                        onArchive = { viewModel.setSubjectArchived(it.id, true) }
                    )
                }

                if (showAdd) {
                    SubjectEditorDialog (
                        onDismiss = { showAdd = false },
                        onSave = { name, color ->
                            viewModel.addSubject(name, color)
                            showAdd = false
                        }
                    )
                }

                state.subjects.firstOrNull { it.id == editId }?.let { subject ->
                    SubjectEditorDialog (
                        initialSubject = subject,
                        onDismiss = { editId = null },
                        onSave = { name, color ->
                            viewModel.updateSubject(subject.id, name, color)
                            editId = null
                        }
                    )
                }

                state.subjects.firstOrNull { it.id == deleteId }?.let { subject ->
                    DeleteSubjectDialog(
                        subject = subject,
                        onDismiss = { deleteId = null },
                        onConfirm = {
                            viewModel.deleteSubject(subject.id)
                            deleteId = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimerHome(
    state: HomeUIState.Success,
    onStart: (Subject) -> Unit,
    onOpenEditor: () -> Unit,
    onOpenBooks: (() -> Unit)?,
    onEdit: (Subject) -> Unit,
    onDelete: (Subject) -> Unit,
    onArchive: (Subject) -> Unit
) {
    val today = state.subjects.sumOf { it.timeSpentSeconds } +
            state.deletedSubjectStudySeconds
    val dateText = SimpleDateFormat("EEE, M/d", Locale.getDefault()).format(Date())

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxWidth().background(YptPanel).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.fillMaxWidth().height(56.dp)) {
                DayBadge(Modifier.align(Alignment.CenterStart))
                Text(dateText, Modifier.align(Alignment.Center), fontSize = 16.sp)
                MoreFeaturesMenu(onOpenBooks, Modifier.align(Alignment.CenterEnd))
            }

            Spacer(Modifier.height(22.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(clockText(today), fontSize = 44.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = restText(state.currentRestTime + state.currentBreakSeconds),
                color = YptMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(18.dp))
        }

        Row(Modifier.fillMaxWidth()
        ) {
            Column(Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(Modifier.height(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Timer", color = YptInk)
                }
            }
        }

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(state.subjects.filterNot { it.isArchived }, key = { it.id }) { subject ->
                SubjectRow(
                    subject = subject,
                    onPlay = { onStart(subject) },
                    onEdit = { onEdit(subject) },
                    onDelete = { onDelete(subject) },
                    onArchive = { onArchive(subject) }
                )
            }

            item {
                FilledTonalButton(
                    onClick = onOpenEditor,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Edit subject", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun DayBadge(modifier: Modifier = Modifier) {
    Text(
        text = "D-?",
        modifier = modifier
            .background(Color(0xFFECECEC), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun EditSubjectsScreen(
    subjects: List<Subject>,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Subject) -> Unit,
    onDelete: (Subject) -> Unit,
    onArchive: (Subject) -> Unit
) {
    val studying = subjects.filterNot { it.isArchived }
    val archived = subjects.filter { it.isArchived }

    Column(Modifier.fillMaxSize().background(YptEditor).padding(horizontal = 18.dp)) {
        Box(Modifier.fillMaxWidth().height(56.dp)) {
            PlaceholderButton(
                symbol = "‹",
                label = "Back",
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text("Edit subject", Modifier.align(Alignment.Center), fontSize = 17.sp)
        }

        LazyColumn(contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)) {
            item {
                Text("Studying subjects ${studying.size}", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))

                Surface(shape = RoundedCornerShape(10.dp), color = Color.White, contentColor = YptInk) {
                    Column {
                        studying.forEach { subject ->
                            key(subject.id) {
                                SubjectRow(
                                    subject = subject,
                                    onEdit = { onEdit(subject) },
                                    onDelete = { onDelete(subject) },
                                    onArchive = { onArchive(subject) }
                                )
                            }
                        }
                        HorizontalDivider()
                        TextButton(
                            onClick = onAdd,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("+ Add subject", color = YptMuted, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(22.dp))
                Text("Archived ${archived.size}", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))

                if (archived.isNotEmpty()) {
                    Surface(shape = RoundedCornerShape(10.dp), color = Color.White, contentColor = YptInk) {
                        Column {
                            archived.forEach { subject ->
                                key(subject.id) {
                                    SubjectRow(
                                        subject = subject,
                                        onEdit = { onEdit(subject) },
                                        onDelete = { onDelete(subject) },
                                        onArchive = { onArchive(subject) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectRow(
    subject: Subject,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onArchive: () -> Unit,
    onPlay: (() -> Unit)? = null
) {
    var menuOpen by remember { mutableStateOf(false) }
    val color = subjectColor(subject.colorHex)

    Row(
        Modifier.fillMaxWidth().heightIn(min = 56.dp)
            .padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onPlay != null) {
            IconButton(
                onClick = onPlay,
                modifier = Modifier.semantics { contentDescription = "Start ${subject.name}" }
            ) {
                Box(
                    Modifier.size(30.dp).background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("▶", color = Color.White, fontSize = 14.sp)
                }
            }
        } else {
            Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.size(13.dp).background(color, CircleShape))
            }
        }

        Text(
            subject.name,
            modifier = Modifier.weight(1f).padding(start = 6.dp, end = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp
        )

        if (onPlay != null) {
            Text(clockText(subject.timeSpentSeconds), fontSize = 16.sp)
        }

        Box {
            PlaceholderButton("⋮", "Options for ${subject.name}", { menuOpen = true })
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = { menuOpen = false; onEdit() }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = { menuOpen = false; onDelete() }
                )
                DropdownMenuItem(
                    text = { Text(if (subject.isArchived) "Restore" else "Archive") },
                    onClick = { menuOpen = false; onArchive() }
                )
            }
        }
    }
}

@Composable
private fun PlaceholderButton(
    symbol: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.semantics { contentDescription = label }
    ) {
        Text(symbol, fontSize = 23.sp)
    }
}

@Composable
private fun SubjectEditorDialog(
    initialSubject: Subject? = null,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by rememberSaveable(initialSubject?.id) {
        mutableStateOf(initialSubject?.name.orEmpty())
    }
    var colorHex by rememberSaveable(initialSubject?.id) {
        mutableStateOf(initialSubject?.colorHex ?: "#9E9E9E")
    }
    var showColors by rememberSaveable { mutableStateOf(false) }
    val save = { onSave(name.trim(), colorHex) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        Box(Modifier.fillMaxSize().imePadding().padding(top = 24.dp)) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxSize(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    Modifier.verticalScroll(rememberScrollState()).padding(18.dp)
                ) {
                    Box(Modifier.fillMaxWidth().height(48.dp)) {
                        FilledIconButton(
                            onClick = onDismiss,
                            modifier = Modifier.align(Alignment.CenterStart)
                                .semantics { contentDescription = "Close subject editor" },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text("×", style = MaterialTheme.typography.titleLarge)
                        }

                        Text(
                            if (initialSubject == null) "Subject name" else "Edit subject",
                            Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.titleMedium
                        )

                        FilledIconButton(
                            onClick = save,
                            enabled = name.isNotBlank(),
                            modifier = Modifier.align(Alignment.CenterEnd)
                                .semantics { contentDescription = "Save subject" }
                        ) {
                            Text("✓", style = MaterialTheme.typography.titleLarge)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                            .semantics { contentDescription = "Subject name" },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        placeholder = { Text("e.g. math, English, science, history…") },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { if (name.isNotBlank()) save() }
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    Surface(
                        onClick = {
                            focusManager.clearFocus()
                            showColors = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            Modifier.heightIn(min = 52.dp).padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subject color", style = MaterialTheme.typography.bodyLarge)
                            Box(Modifier.size(36.dp).background(subjectColor(colorHex), CircleShape))
                        }
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            if (initialSubject == null && !showColors) {
                focusRequester.requestFocus()
            }
        }
    }

    if (showColors) {
        SubjectColorDialog(
            initialColor = colorHex,
            onDismiss = { showColors = false },
            onConfirm = {
                colorHex = it
                showColors = false
            }
        )
    }
}

@Composable
private fun DeleteSubjectDialog(
    subject: Subject,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text("Delete subject", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Are you sure you want to delete '${subject.name}'?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Your study time logs won’t be deleted.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}

@Composable
private fun StudyScreen(
    state: HomeUIState.Success,
    subject: Subject,
    onPause: () -> Unit,
    onOpenBooks: (() -> Unit)?
) {
    var showGroupView by rememberSaveable(subject.id) { mutableStateOf(true) }

    val sessionTime = clockText(state.currentFocusTime)
    val subjectTime = clockText(subject.timeSpentSeconds + state.currentFocusTime)
    val todayTime = clockText(
        state.subjects.sumOf { it.timeSpentSeconds } +
                state.deletedSubjectStudySeconds +
                state.currentFocusTime
    )

    BackHandler (onBack = onPause)

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(start = 18.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showGroupView) {
                Text(
                    "${subject.name}  $subjectTime",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            } else {
                DayBadge()
                Spacer(Modifier.weight(1f))
            }

            PlaceholderButton("⇄", "Switch study view", { showGroupView = !showGroupView })
            MoreFeaturesMenu(onOpenBooks)
        }

        if (showGroupView) {
            GroupStudyContent(
                modifier = Modifier.weight(1f),
                sessionTime = sessionTime,
                running = state.isTimerRunning,
                onToggleTimer = onPause
            )
        } else {
            SoloStudyContent(
                modifier = Modifier.weight(1f),
                subjectName = subject.name,
                sessionTime = sessionTime,
                subjectTime = subjectTime,
                todayTime = todayTime,
                running = state.isTimerRunning,
                onToggleTimer = onPause
            )
        }
    }
}

@Composable
private fun GroupStudyContent(
    modifier: Modifier,
    sessionTime: String,
    running: Boolean,
    onToggleTimer: () -> Unit
) {
    Column(modifier.padding(horizontal = 10.dp)) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = YptPanel,
            contentColor = YptInk
        ) {
            Row(
                Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Current focus time", fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(sessionTime, fontSize = 33.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        PauseButton(running, onToggleTimer)
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    DeskPlaceholder()
                    Text(sessionTime, color = YptMuted)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(14.dp),
            color = YptPanel,
            contentColor = YptInk
        ) {
            Box(Modifier.fillMaxSize()) {
                Column(
                    Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                        .padding(14.dp).padding(bottom = 48.dp)
                ) {
                    // connect with group data later
                    Text("Study Squad", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Text(if (running) "1 member studying" else "0 members studying")
                    Spacer(Modifier.height(18.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        listOf("Aisha", "You", "Member 3", "Member 4").forEach { name ->
                            Column(
                                Modifier.weight(1f).padding(2.dp).then(
                                    if (name == "You") {
                                        Modifier.border(1.dp, Color.LightGray, RoundedCornerShape(5.dp))
                                    } else {
                                        Modifier
                                    }
                                ).padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                DeskPlaceholder()
                                Text(name, color = YptMuted, fontSize = 12.sp, maxLines = 1)
                                Text(
                                    if (name == "You") sessionTime else "0:00:00",
                                    color = YptMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                AllowedAppsPlaceholder(Modifier.align(Alignment.BottomEnd).padding(bottom = 12.dp))
            }
        }
    }
}

@Composable
private fun SoloStudyContent(
    modifier: Modifier,
    subjectName: String,
    sessionTime: String,
    subjectTime: String,
    todayTime: String,
    running: Boolean,
    onToggleTimer: () -> Unit
) {
    Column(modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (running) "Focusing" else "Paused", fontSize = 16.sp)
                Text(sessionTime, fontSize = 50.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))

                Row(
                    Modifier.fillMaxWidth(0.66f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StudyTotal(subjectName, subjectTime, Modifier.weight(1f))
                    StudyTotal("Today", todayTime, Modifier.weight(1f))
                }
            }
        }

        Box(Modifier.weight(0.85f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            DeskPlaceholder(Modifier.size(80.dp))
        }

        AllowedAppsPlaceholder(Modifier.align(Alignment.End))
        Spacer(Modifier.height(10.dp))
        PauseButton(
            running = running,
            onClick = onToggleTimer,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            diameter = 48.dp
        )
    }
}

@Composable
private fun StudyTotal(label: String, time: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = YptMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(time, color = YptMuted)
    }
}

@Composable
private fun PauseButton(
    running: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    diameter: Dp = 40.dp
) {
    val label = if (running) "Pause" else "Resume"
    IconButton(
        onClick = onClick,
        modifier = modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .semantics { contentDescription = label }
    ) {
        Box(Modifier.size(diameter).background(YptInk, CircleShape), contentAlignment = Alignment.Center) {
            Text(if (running) "Ⅱ" else "▶", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
private fun DeskPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text("▧", color = Color(0xFF999999), fontSize = 29.sp)
    }
}

@Composable
private fun AllowedAppsPlaceholder(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White,
        contentColor = YptInk,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(
            topStart = 24.dp,
            bottomStart = 24.dp
        )
    ) {
        Text("Allowed Apps", Modifier.padding(horizontal = 18.dp, vertical = 9.dp))
    }
}

@Composable
private fun BreakLogDialog(
    seconds: Long,
    onTagSelected: (String) -> Unit,
    onNoLog: () -> Unit
) {
    val duration = "${seconds / 3_600}h ${seconds % 3_600 / 60}m ${seconds % 60}s"

    Dialog(onDismissRequest = onNoLog) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Break log $duration",
                    style = MaterialTheme.typography.titleLarge
                )

                listOf(
                    listOf("meals", "bathroom"),
                    listOf("rest", "other")
                ).forEach { tags ->
                    Row(Modifier.fillMaxWidth()) {
                        tags.forEach { tag ->
                            TextButton(
                                onClick = { onTagSelected(tag) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("#$tag", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(48.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {}, enabled = false) { Text("Edit tags") }
                    TextButton(onClick = onNoLog) { Text("No log") }
                }
            }
        }
    }
}

@Composable
private fun MoreFeaturesMenu(
    onOpenBooks: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        PlaceholderButton("▦", "More features", { expanded = true })

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Books") },
                enabled = onOpenBooks != null,
                onClick = {
                    expanded = false
                    onOpenBooks?.invoke()
                }
            )
            DropdownMenuItem(
                text = { Text("Stats") },
                enabled = false,
                onClick = {}
            )
            DropdownMenuItem(
                text = { Text("Rankings") },
                enabled = false,
                onClick = {}
            )
        }
    }
}

private val subjectPalette = listOf(
    "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
    "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50",
    "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800",
    "#FF5722", "#795548", "#9E9E9E", "#607D8B"
)

@Composable
private fun SubjectColorDialog(
    initialColor: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedColor by rememberSaveable(initialColor) { mutableStateOf(initialColor) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(14.dp)
            ) {
                Text(
                    "Select color",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))

                subjectPalette.chunked(5).forEach { row ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        row.forEach { hex ->
                            val selected = hex.equals(selectedColor, ignoreCase = true)

                            Surface(
                                onClick = { selectedColor = hex },
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                                    .semantics {
                                        contentDescription =
                                            if (selected) "Selected color $hex" else "Color $hex"
                                    },
                                shape = CircleShape,
                                color = subjectColor(hex),
                                shadowElevation = 3.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (selected) {
                                        Text(
                                            "✓",
                                            color = Color.Black,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    }
                                }
                            }
                        }

                        repeat(5 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onConfirm(selectedColor) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
private fun MockPage1Preview() {
    val viewModel = HomeViewModel()
    MaterialTheme { HomePage(Modifier, viewModel) }
}