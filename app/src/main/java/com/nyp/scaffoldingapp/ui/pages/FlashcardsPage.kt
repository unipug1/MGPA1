package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.window.DialogProperties

@Composable
fun FlashcardsPage(
    modifier: Modifier = Modifier,
    viewModel: FlashcardsViewModel,
    onBack: () -> Unit = {}
) {
    val decks by viewModel.decks.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val flashcards by viewModel.flashcards.collectAsState()

    var newOptionsExpanded by rememberSaveable { mutableStateOf(false) }
    var moreOptionsExpanded by rememberSaveable { mutableStateOf(false) }
    var showAddDeckDialog by rememberSaveable { mutableStateOf(false) }
    var showAddFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showCardEditor by rememberSaveable { mutableStateOf(false) }
    var showArchived by rememberSaveable { mutableStateOf(false) }
    var editingFlashcardId by rememberSaveable { mutableStateOf<String?>(null) }

    var selectedDeckId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedDeck = decks.firstOrNull { it.id == selectedDeckId }
    val deckFlashcards = flashcards.filter { it.deckId == selectedDeckId }
    val editingFlashcard = deckFlashcards.firstOrNull { it.id == editingFlashcardId }

    if (selectedDeck != null) {
        if (showCardEditor) {
            CardEditorPage(
                deck = selectedDeck,
                viewModel = viewModel,
                editingFlashcard = editingFlashcard,
                onBack = { showCardEditor = false }
            )
        } else {
            DeckDetailPage(
                deck = selectedDeck,
                flashcards = deckFlashcards,
                onBack = {
                    selectedDeckId = null
                    showCardEditor = false
                },
                onAddCard = {
                    editingFlashcardId = null
                    showCardEditor = true
                },
                onDeleteCard = { flashcardId ->
                    viewModel.deleteFlashcard(flashcardId)
                },
                onEditCard = { flashcardId ->
                    editingFlashcardId = flashcardId
                    showCardEditor = true
                }
            )
        }
    } else {
        Column(modifier = modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth()
                .height(64.dp)
            ) {
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(
                        text = "<",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Text(
                    text = "Flashcards",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    FilledTonalButton(
                        onClick = { newOptionsExpanded = true }
                    ) {
                        Text(
                            text = "+ New",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    DropdownMenu(
                        expanded = newOptionsExpanded,
                        onDismissRequest = {
                            newOptionsExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Add deck") },
                            onClick = {
                                newOptionsExpanded = false
                                showAddDeckDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Add folder") },
                            onClick = {
                                newOptionsExpanded = false
                                showAddFolderDialog = true
                            }
                        )
                    }
                }
                Box {
                    FilledTonalButton(
                        onClick = { moreOptionsExpanded = true },
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("···")
                    }
                    DropdownMenu(
                        expanded = moreOptionsExpanded,
                        onDismissRequest = {
                            moreOptionsExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort by") },
                            onClick = {
                                moreOptionsExpanded = false
                                // TODO: Sorting
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Archived") },
                            onClick = {
                                moreOptionsExpanded = false
                                showArchived = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("List") },
                            onClick = {
                                moreOptionsExpanded = false
                                // TODO: Switch list/grid
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()
                .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(key = "folders_header") {
                    Text(
                        text = "Folders",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    )
                }
                items(items = folders, key = {it.id}) { folder ->
                    var folderOptionsExpanded by rememberSaveable { mutableStateOf(false) }
                    Card(modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()
                            .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)
                            ) {
                                Text(folder.name)
                                Text(folder.author)
                            }
                            Box {
                                IconButton(onClick = { folderOptionsExpanded = true }
                                ) {
                                    Text("⋮")
                                }
                                DropdownMenu(
                                    expanded = folderOptionsExpanded,
                                    onDismissRequest = {
                                        folderOptionsExpanded = false
                                    }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Edit") },
                                        onClick = {
                                            folderOptionsExpanded = false
                                            // TODO: Edit folder
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Delete") },
                                        onClick = {
                                            folderOptionsExpanded = false
                                            // TODO: Delete folder
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Archive") },
                                        onClick = {
                                            folderOptionsExpanded = false
                                            // TODO: Archive folder
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                item(key = "decks_header") {
                    Text(
                        text = "Decks",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    )
                }
                items(items = decks.filter { it.folderId == null }, key = { it.id }) { deck ->
                    var deckOptionsExpanded by rememberSaveable { mutableStateOf(false) }
                    Card(modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp),
                        onClick = {
                            selectedDeckId = deck.id
                        }
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()
                            .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)
                            ) {
                                Text(deck.title)
                                Text(deck.author)
                            }
                            Box {
                                IconButton(onClick = { deckOptionsExpanded = true }
                                ) {
                                    Text("⋮")
                                }
                                DropdownMenu(
                                    expanded = deckOptionsExpanded,
                                    onDismissRequest = {
                                        deckOptionsExpanded = false
                                    }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Edit") },
                                        onClick = {
                                            deckOptionsExpanded = false
                                            // TODO: Edit deck
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Move") },
                                        onClick = {
                                            deckOptionsExpanded = false
                                            // TODO: Move into folder
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Delete") },
                                        onClick = {
                                            deckOptionsExpanded = false
                                            // TODO: Delete deck
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Archive") },
                                        onClick = {
                                            deckOptionsExpanded = false
                                            // TODO: Archive deck
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddFolderDialog) {
        AddFolderDialog(
            availableDecks = decks,
            onDismiss = { showAddFolderDialog = false },
            onSave = { name, author, selectedDeckIds ->
                val newFolderId = viewModel.addFolder(name, author)
                viewModel.moveDecksToFolder(selectedDeckIds, newFolderId)
                showAddFolderDialog = false
            }
        )
    }
    if (showAddDeckDialog) {
        AddDeckDialog(
            onDismiss = { showAddDeckDialog = false },
            onSave = { title, author ->
                val newDeckId = viewModel.addDeck(title, author)
                showAddDeckDialog = false
                selectedDeckId = newDeckId
            }
        )
    }
}

@Composable
private fun AddFolderDialog(
    availableDecks: List<FlashcardDeck>,
    onDismiss: () -> Unit,
    onSave: (String, String, List<String>) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var author by rememberSaveable { mutableStateOf("") }

    var selectedDeckIds by rememberSaveable { mutableStateOf(listOf<String>()) }

    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxWidth()
            .fillMaxHeight(0.95f),
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp,
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            ),
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Column(modifier = Modifier.fillMaxSize()
                .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("✕",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "New folder",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            onSave(
                                name.trim(),
                                author.trim(),
                                selectedDeckIds
                            )
                        },
                        enabled = name.isNotBlank() && author.isNotBlank()
                    ) {
                        Text(
                            "✓",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Enter folder name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                TextField(
                    value = author,
                    onValueChange = { author = it },
                    placeholder = { Text("Author") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Select the flashcards to move to the folder.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // existing decks
                LazyColumn(modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableDecks, key = { it.id }) { deck ->
                        Card(modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = selectedDeckIds.contains(deck.id),
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            selectedDeckIds += deck.id
                                        } else {
                                            selectedDeckIds -= deck.id
                                        }
                                    }
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = deck.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = deck.author,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun AddDeckDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var author by rememberSaveable { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxWidth()
            .fillMaxHeight(0.95f),
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp,
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            ),
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Column(modifier = Modifier.fillMaxSize()
                .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("✕",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Add deck",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            onSave(title.trim(), author.trim())
                        },
                        enabled = title.isNotBlank() && author.isNotBlank()
                    ) {
                        Text(
                            "✓",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                TextField(
                    value = author,
                    onValueChange = { author = it },
                    placeholder = { Text("Author") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
private fun DeckDetailPage(
    deck: FlashcardDeck,
    flashcards: List<Flashcard>,
    onBack: () -> Unit,
    onAddCard: () -> Unit,
    onDeleteCard: (String) -> Unit,
    onEditCard: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text("<")
                }
                Text(
                    text = deck.title,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Surface(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(20.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(modifier = Modifier.size(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { 0f },
                                    modifier = Modifier.fillMaxSize(),
                                    strokeWidth = 12.dp,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Text(
                                    text = "0%",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            FilledTonalButton(
                                onClick = {
                                    // TODO: Open Memorize page
                                }
                            ) {
                                Text("Memorize")
                            }
                        }
                        Column(modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(modifier = Modifier.size(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { 0f },
                                    modifier = Modifier.fillMaxSize(),
                                    strokeWidth = 12.dp,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Text(
                                    text = "0",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            FilledTonalButton(
                                onClick = {
                                    // TODO: Open Test page
                                }
                            ) {
                                Text("Test")
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cards ${flashcards.size}",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(
                    onClick = {
                        // TODO: Open card filter dropdown
                    }
                ) {
                    Text("All ▼")
                }
            }
            if (flashcards.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()
                    .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tap the + button to add flashcards.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()
                    .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(items = flashcards, key = { _, flashcard -> flashcard.id }
                    ) { index, flashcard ->
                        FlashcardRow(
                            flashcard = flashcard,
                            cardNumber = index + 1,
                            onEdit = {
                                onEditCard(flashcard.id)
                            },
                            onDelete = {
                                onDeleteCard(flashcard.id)
                            }
                        )
                    }
                }
            }
        }
        FloatingActionButton(onClick = onAddCard,
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+")
        }
    }
}

@Composable
private fun CardEditorPage(
    deck: FlashcardDeck,
    viewModel: FlashcardsViewModel,
    onBack: () -> Unit,
    editingFlashcard: Flashcard? = null
) {
    var selectedCardType by rememberSaveable(editingFlashcard?.id) {
        mutableStateOf(editingFlashcard?.type ?: CardType.WORD)
    }
    var cardTypeExpanded by rememberSaveable { mutableStateOf(false) }
    val cardTypeLabel = when (selectedCardType) {
        CardType.WORD -> "Word"
        CardType.QUESTION_ANSWER -> "Q&A"
        CardType.TRUE_FALSE -> "True or False"
        CardType.FILL_IN_BLANK -> "Fill in the Blank"
    }
    var question by rememberSaveable(editingFlashcard?.id) {
        mutableStateOf(editingFlashcard?.question ?: "")
    }
    var answer by rememberSaveable(editingFlashcard?.id) {
        mutableStateOf(editingFlashcard?.answer ?: "")
    }
    val canSave = question.isNotBlank() && answer.isNotBlank() &&
            (selectedCardType != CardType.FILL_IN_BLANK || question.contains("____"))

    Column(modifier = Modifier.fillMaxSize()
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
                text = if (editingFlashcard == null) "Add card" else "Edit card",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Surface(modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)
            ) {
                Box {
                    TextButton(
                        onClick = {
                            cardTypeExpanded = true
                        }
                    ) {
                        Text(cardTypeLabel)
                        Text(" ▼")
                    }
                    DropdownMenu(
                        expanded = cardTypeExpanded,
                        onDismissRequest = {
                            cardTypeExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Word") },
                            onClick = {
                                selectedCardType = CardType.WORD
                                cardTypeExpanded = false
                                question = ""
                                answer = ""
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Q&A") },
                            onClick = {
                                selectedCardType = CardType.QUESTION_ANSWER
                                cardTypeExpanded = false
                                question = ""
                                answer = ""
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("True or False") },
                            onClick = {
                                selectedCardType = CardType.TRUE_FALSE
                                cardTypeExpanded = false
                                question = ""
                                answer = ""
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Fill in the blank") },
                            onClick = {
                                selectedCardType = CardType.FILL_IN_BLANK
                                cardTypeExpanded = false
                                question = ""
                                answer = ""
                            }
                        )
                    }
                }
                when (selectedCardType) {
                    CardType.WORD -> {
                        TextField(
                            value = question,
                            onValueChange = { question = it },
                            placeholder = { Text("Word") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        TextField(
                            value = answer,
                            onValueChange = { answer = it },
                            placeholder = { Text("Meaning") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    CardType.QUESTION_ANSWER -> {
                        TextField(
                            value = question,
                            onValueChange = { question = it },
                            placeholder = { Text("Question") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        TextField(
                            value = answer,
                            onValueChange = { answer = it },
                            placeholder = { Text("Answer") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    CardType.TRUE_FALSE -> {
                        TextField(
                            value = question,
                            onValueChange = { question = it },
                            placeholder = { Text("Enter a statement") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilterChip(
                                selected = answer == "true",
                                onClick = { answer = "true" },
                                label = { Text("True") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = answer == "false",
                                onClick = { answer = "false" },
                                label = { Text("False") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    CardType.FILL_IN_BLANK -> {
                        TextField(
                            value = question,
                            onValueChange = { question = it },
                            placeholder = { Text("Enter a sentence using ____ for the blank") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        Spacer(Modifier.height(12.dp))
                        TextField(
                            value = answer,
                            onValueChange = { answer = it },
                            placeholder = { Text("Enter the missing word or phrase") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (editingFlashcard == null) {
                    viewModel.addFlashcard(
                        deckId = deck.id,
                        type = selectedCardType,
                        question = question.trim(),
                        answer = answer.trim()
                    )
                    question = ""
                    answer = ""
                } else {
                    viewModel.updateFlashcard(
                        flashcardId = editingFlashcard.id,
                        type = selectedCardType,
                        question = question.trim(),
                        answer = answer.trim()
                    )
                    onBack()
                }
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                if (editingFlashcard == null) "Save card" else "Save changes"
            )
        }
    }
}

@Composable
private fun FlashcardRow(
    flashcard: Flashcard,
    cardNumber: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var optionsExpanded by rememberSaveable { mutableStateOf(false) }
    val typeLabel = when (flashcard.type) {
        CardType.WORD -> "Word"
        CardType.QUESTION_ANSWER -> "Q&A"
        CardType.TRUE_FALSE -> "True or False"
        CardType.FILL_IN_BLANK -> "Fill in the Blank"
    }

    Card(modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$cardNumber. $typeLabel",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box {
                    IconButton(
                        onClick = { optionsExpanded = true }
                    ) {
                        Text("⋮")
                    }
                    DropdownMenu(
                        expanded = optionsExpanded,
                        onDismissRequest = {
                            optionsExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                optionsExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                optionsExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
            Text(
                text = flashcard.question,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = flashcard.answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// preview
@Preview
@Composable
private fun MockFlashcardsPagePreview() {
    MaterialTheme { FlashcardsPage(Modifier, FlashcardsViewModel()) }
}