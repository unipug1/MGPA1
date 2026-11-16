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
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
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
    else -> "${seconds / 3_600}h ${seconds % 3_600 / 60}m"
}

private fun subjectColor(hex: String): Color =
    runCatching { Color(hex.toColorInt()) }.getOrDefault(Color.Gray)

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
                    activeSubject != null -> StudyScreen(
                        state = state,
                        subject = activeSubject,
                        onToggleTimer = viewModel::toggleTimer,
                        onFinish = viewModel::finishTimer
                    )

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
                    AddSubjectDialog(
                        onDismiss = { showAdd = false },
                        onSave = { name, color ->
                            viewModel.addSubject(name, color)
                            showAdd = false
                        }
                    )
                }

                state.subjects.firstOrNull { it.id == editId }?.let { subject ->
                    AddSubjectDialog(
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
    val today = state.subjects.sumOf{it.timeSpentSeconds}
    val dateText = SimpleDateFormat("EEE, M/d", Locale.getDefault()).format(Date())
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxWidth().background(YptPanel).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.fillMaxWidth().height(56.dp)) {
                DayBadge(Modifier.align(Alignment.CenterStart))
                Text(dateText, Modifier.align(Alignment.Center), fontSize = 16.sp)
                // more icon
                Text("▦", Modifier.align(Alignment.CenterEnd), fontSize = 24.sp)
            }

            Spacer(Modifier.height(22.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(clockText(today), fontSize = 44.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Text(restText(state.currentRestTime), color = YptMuted)
            Spacer(Modifier.height(18.dp))
        }

        Row(Modifier.fillMaxWidth()) {
            listOf("Timer", "Books").forEach { label ->
                val tabModifier = Modifier.weight(1f)
                val canOpenBooks = label == "Books" && onOpenBooks != null

                Column(
                    modifier = if (canOpenBooks) {
                        tabModifier.clickable { onOpenBooks?.invoke() }
                    } else {
                        tabModifier
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(Modifier.height(46.dp), contentAlignment = Alignment.Center) {
                        Text(label, color = if (label == "Timer") YptInk else YptMuted)
                    }
                    Box(
                        Modifier.width(34.dp).height(2.dp)
                            .background(if (label == "Timer") YptInk else Color.Transparent)
                    )
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
                TextButton(onClick = onOpenEditor, modifier = Modifier.padding(start = 12.dp)) {
                    Text("Edit subject", color = YptInk)
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
                            androidx.compose.runtime.key(subject.id) {
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
                            Text("＋  Add subject", color = YptMuted, modifier = Modifier.weight(1f))
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
                                androidx.compose.runtime.key(subject.id) {
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
                    leadingIcon = { Text("✎") },
                    onClick = { menuOpen = false; onEdit() }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    leadingIcon = { Text("×") },
                    onClick = { menuOpen = false; onDelete() }
                )
                DropdownMenuItem(
                    text = { Text(if (subject.isArchived) "Restore" else "Archive") },
                    leadingIcon = { Text("▣") },
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
fun AddSubjectDialog(
    initialSubject: Subject? = null, // null for add, non-null for edit
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var subjectName by rememberSaveable { mutableStateOf(initialSubject?.name ?: "") }
    val subjectNameFocusRequester = remember { FocusRequester() }
    var subjectColor by rememberSaveable { mutableStateOf(initialSubject?.colorHex ?: "#9E9E9E") }
    var showSelectColorDialogue by rememberSaveable { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = onDismiss) {
                        Text("✗")
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = if (initialSubject == null) "Subject name" else "Edit",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.weight(1f))
                    Button(onClick = { onSave(subjectName.trim(), subjectColor) },
                        enabled = subjectName.isNotBlank()
                    ) {
                        Text("✓")
                    }
                }

                // subject name
                Spacer(Modifier.height(30.dp))
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = {subjectName = it},
                        modifier = Modifier.fillMaxWidth()
                            .focusRequester(subjectNameFocusRequester),
                        placeholder = {Text("e.g. math, english, science, geography...")}
                    )
                    LaunchedEffect(Unit) {
                        subjectNameFocusRequester.requestFocus()
                    }
                }

                // subject color
                Spacer(Modifier.padding(30.dp))
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Select Color")

                        val parsedColor = try {
                            Color(subjectColor.toColorInt())
                        } catch (e:Exception) {
                            Color.Gray
                        }
                        // color wheel
                        Box(modifier = Modifier
                            .size(24.dp)
                            .background(color = parsedColor, shape = CircleShape)
                            .clickable{ showSelectColorDialogue = true}
                        )
                    }
                }
            }
        }
    }

    // add more colors later
    val availableColors = listOf(
        "#EA1E63", // red pink
        "#FD9800", // light orange
        "#FD5723", // orange
        "#FFEB3C", // pale yellow
        "#7A574D", // brown
        "#9E9E9E" // gray
    )

    if (showSelectColorDialogue) {
        var tempSelectedColor by rememberSaveable {mutableStateOf(subjectColor)}

        Dialog(onDismissRequest = { showSelectColorDialogue = false }) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ) {
                    Spacer(Modifier.height(16.dp))
                    Text("Select color", style = MaterialTheme.typography.headlineSmall)

                    // color selection
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(availableColors) { colorHex ->
                            val colorInt = try {
                                Color(colorHex.toColorInt())
                            } catch (e: Exception) {
                                Color.Gray
                            }

                            Box(Modifier
                                .size(40.dp)
                                .background(colorInt, shape = CircleShape)
                                .clickable { tempSelectedColor = colorHex},
                                contentAlignment = Alignment.Center
                            ) {
                                if (colorHex == tempSelectedColor) {
                                    Text("✓", color = Color.Black)
                                }
                            }
                        }
                    }

                    // buttons
                    Row(Modifier.fillMaxWidth()){
                        // 1: cancel
                        Button(onClick = {showSelectColorDialogue = false},
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant // ensures text is readable
                            )) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.weight(1f))
                        // 2: ok
                        Button(onClick = {subjectColor = tempSelectedColor
                            showSelectColorDialogue = false},
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant // ensures text is readable
                            )) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteSubjectDialog(
    subject: Subject,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Column(Modifier
                .fillMaxWidth()
            ) {
                // title
                Text("Delete subject", style = MaterialTheme.typography.headlineMedium)

                // content
                Text("Are you sure you want to delete '${subject.name}'? All To-Dos in this subject will be deleted.")
                Spacer(Modifier.height(10.dp))
                Text("Your study time logs won't be deleted.")

                // buttons
                Row(Modifier.fillMaxWidth()) {
                    // 1: cancel
                    Button(onClick = { onDismiss() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant // ensures text is readable
                        )) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.weight(1f))
                    // 2: ok
                    Button(onClick = { onConfirm() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant // ensures text is readable
                        )) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyScreen(
    state: HomeUIState.Success,
    subject: Subject,
    onToggleTimer: () -> Unit,
    onFinish: () -> Unit
) {
    var showGroupView by rememberSaveable(subject.id) { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }
    var showFinishDialog by rememberSaveable { mutableStateOf(false) }

    val sessionTime = clockText(state.currentFocusTime)
    val subjectTime = clockText(subject.timeSpentSeconds + state.currentFocusTime)
    val todayTime = clockText(
        state.subjects.sumOf { it.timeSpentSeconds } + state.currentFocusTime
    )

    BackHandler { showFinishDialog = true }

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
            Box {
                PlaceholderButton("▦", "Study options", { showMenu = true })
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Finish session") },
                        onClick = {
                            showMenu = false
                            showFinishDialog = true
                        }
                    )
                }
            }
        }

        if (showGroupView) {
            GroupStudyContent(
                modifier = Modifier.weight(1f),
                sessionTime = sessionTime,
                running = state.isTimerRunning,
                onToggleTimer = onToggleTimer
            )
        } else {
            SoloStudyContent(
                modifier = Modifier.weight(1f),
                subjectName = subject.name,
                sessionTime = sessionTime,
                subjectTime = subjectTime,
                todayTime = todayTime,
                running = state.isTimerRunning,
                onToggleTimer = onToggleTimer
            )
        }
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finish session?") },
            text = { Text("Your focus time will be added to ${subject.name}.") },
            confirmButton = {
                TextButton(onClick = {
                    showFinishDialog = false
                    onFinish()
                }) { Text("Finish") }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) { Text("Cancel") }
            }
        )
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
    diameter: androidx.compose.ui.unit.Dp = 40.dp
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
private fun DeskPlaceholder(modifier: Modifier = Modifier.size(44.dp)) {
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

// Preview
@Preview(showBackground = true)
@Composable
private fun MockPage1Preview() {
    val viewModel = HomeViewModel()
    MaterialTheme { HomePage(Modifier, viewModel) }
}