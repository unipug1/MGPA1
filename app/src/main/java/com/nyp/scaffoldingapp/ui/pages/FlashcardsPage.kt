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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.remember

@Composable
fun FlashcardsPage(
    modifier: Modifier = Modifier,
    viewModel: FlashcardsViewModel,
    onBack: () -> Unit = {}
) {
    val decks by viewModel.decks.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val flashcards by viewModel.flashcards.collectAsState()
    val memorisedCardIds by viewModel.memorisedCardIds.collectAsState()

    var newOptionsExpanded by rememberSaveable { mutableStateOf(false) }
    var showAddDeckDialog by rememberSaveable { mutableStateOf(false) }
    var editingDeckId by rememberSaveable { mutableStateOf<String?>(null) }
    val editingDeck = decks.firstOrNull { it.id == editingDeckId }
    var showAddFolderDialog by rememberSaveable { mutableStateOf(false) }
    var editingFolderId by rememberSaveable { mutableStateOf<String?>(null) }
    var deletingFolderId by rememberSaveable { mutableStateOf<String?>(null) }
    val deletingFolder = folders.firstOrNull { it.id == deletingFolderId }
    val editingFolder = folders.firstOrNull { it.id == editingFolderId }
    var showCardEditor by rememberSaveable { mutableStateOf(false) }
    var editingFlashcardId by rememberSaveable { mutableStateOf<String?>(null) }
    var movingDeckId by rememberSaveable { mutableStateOf<String?>(null) }
    val movingDeck = decks.firstOrNull { it.id == movingDeckId }
    var selectedMoveFolderId by rememberSaveable { mutableStateOf<String?>(null) }

    var showMemoriseSetup by rememberSaveable { mutableStateOf(false) }
    var showMemorisePage by rememberSaveable { mutableStateOf(false) }
    var memoriseUnmemorisedOnly by rememberSaveable { mutableStateOf(true) }
    var memoriseRandomOrder by rememberSaveable { mutableStateOf(false) }

    var selectedFolderId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedFolder = folders.firstOrNull { it.id == selectedFolderId }
    var selectedDeckId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedDeck = decks.firstOrNull { it.id == selectedDeckId }
    val deckFlashcards = flashcards.filter { it.deckId == selectedDeckId }
    val editingFlashcard = deckFlashcards.firstOrNull { it.id == editingFlashcardId }
    var deletingDeckId by rememberSaveable { mutableStateOf<String?>(null) }
    val deletingDeck = decks.firstOrNull { it.id == deletingDeckId }

    val unmemorisedCardCount = deckFlashcards.count { flashcard ->
        flashcard.id !in memorisedCardIds
    }

    if (selectedDeck != null) {
        if (showMemorisePage) {
            val cardsToMemorise = remember(selectedDeck.id) {
                val filteredCards = if (memoriseUnmemorisedOnly) {
                    deckFlashcards.filter { flashcard ->
                        flashcard.id !in memorisedCardIds
                    }
                } else {
                    deckFlashcards
                }

                if (memoriseRandomOrder) {
                    filteredCards.shuffled()
                } else {
                    filteredCards
                }
            }
            MemorisePage(
                flashcards = cardsToMemorise,
                onBack = {
                    showMemorisePage = false
                },
                onFinish = { results ->
                    results.forEach { (flashcardId, isMemorised) ->
                        viewModel.setCardMemorised(
                            flashcardId = flashcardId,
                            isMemorised = isMemorised
                        )
                    }

                    showMemorisePage = false
                }
            )
        } else if (showCardEditor) {
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
                memorisedCardIds = memorisedCardIds,
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
                },
                onMemorise = { showMemoriseSetup = true }
            )
        }
    } else if (selectedFolder != null) {
        val folderDecks = decks.filter { deck -> deck.folderId == selectedFolder.id }
        Column(modifier = modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth()
                .height(64.dp)
            ) {
                IconButton(
                    onClick = {
                        selectedFolderId = null
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text("<")
                }
                Text(
                    text = selectedFolder.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Decks (${folderDecks.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )
            if (folderDecks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No decks in this folder.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()
                    .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = folderDecks, key = { it.id }) { deck ->
                        DeckRow(deck = deck,
                            onOpen = {
                                selectedDeckId = deck.id
                            },
                            onEdit = {
                                editingDeckId = deck.id
                                showAddDeckDialog = true
                            },
                            onMove = {
                                movingDeckId = deck.id
                                selectedMoveFolderId = deck.folderId
                            },
                            onDelete = {
                                deletingDeckId = deck.id
                            }
                        )
                    }
                }
            }
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
                                editingDeckId = null
                                showAddDeckDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Add folder") },
                            onClick = {
                                newOptionsExpanded = false
                                editingFolderId = null
                                showAddFolderDialog = true
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
                        .padding(horizontal = 16.dp),
                        onClick = {
                            selectedFolderId = folder.id
                        }
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
                                            editingFolderId = folder.id
                                            showAddFolderDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Delete") },
                                        onClick = {
                                            folderOptionsExpanded = false
                                            folderOptionsExpanded = false
                                            deletingFolderId = folder.id
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
                    DeckRow(deck = deck,
                        onOpen = {
                            selectedDeckId = deck.id
                        },
                        onEdit = {
                            editingDeckId = deck.id
                            showAddDeckDialog = true
                        },
                        onMove = {
                            movingDeckId = deck.id
                            selectedMoveFolderId = deck.folderId
                        },
                        onDelete = {
                            deletingDeckId = deck.id
                        }
                    )
                }
            }
        }
    }

    if (showAddFolderDialog) {
        AddFolderDialog(
            availableDecks = decks,
            editingFolder = editingFolder,
            onDismiss = {
                showAddFolderDialog = false
                editingFolderId = null
            },
            onSave = { name, author, selectedDeckIds ->

                val folderId = editingFolderId

                if (folderId == null) {
                    // Create a new folder
                    val newFolderId = viewModel.addFolder(
                        name = name,
                        author = author
                    )

                    viewModel.moveDecksToFolder(
                        deckIds = selectedDeckIds,
                        folderId = newFolderId
                    )
                } else {
                    // Edit the existing folder
                    viewModel.updateFolder(
                        folderId = folderId,
                        name = name,
                        author = author
                    )

                    viewModel.updateFolderDecks(
                        folderId = folderId,
                        selectedDeckIds = selectedDeckIds
                    )
                }

                showAddFolderDialog = false
                editingFolderId = null
            }
        )
    }
    if (showAddDeckDialog) {
        AddDeckDialog(
            editingDeck = editingDeck,

            onDismiss = {
                showAddDeckDialog = false
                editingDeckId = null
            },

            onSave = { title, author ->
                val deckId = editingDeckId

                if (deckId == null) {
                    // Create a new deck
                    val newDeckId = viewModel.addDeck(
                        title = title,
                        author = author
                    )

                    selectedDeckId = newDeckId
                } else {
                    // Edit an existing deck
                    viewModel.updateDeck(
                        deckId = deckId,
                        title = title,
                        author = author
                    )
                }

                showAddDeckDialog = false
                editingDeckId = null
            }
        )
    }
    if (showMemoriseSetup && selectedDeck != null) {
        MemoriseSetupDialog(
            cardCount = deckFlashcards.size,
            unmemorisedCardCount = unmemorisedCardCount,
            onDismiss = {
                showMemoriseSetup = false
            },
            onStart = { unmemorisedOnly, randomOrder ->
                memoriseUnmemorisedOnly = unmemorisedOnly
                memoriseRandomOrder = randomOrder

                showMemoriseSetup = false
                showMemorisePage = true
            }
        )
    }
    if (deletingFolder != null) {
        AlertDialog(
            onDismissRequest = { deletingFolderId = null },
            title = { Text(deletingFolder.name) },
            text = { Text("Are you sure you want to delete this folder?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteFolder(deletingFolder.id)
                        deletingFolderId = null
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        deletingFolderId = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    if (deletingDeck != null) {
        AlertDialog(
            onDismissRequest = {
                deletingDeckId = null
            },

            title = {
                Text(
                    text = deletingDeck.title,
                    style = MaterialTheme.typography.titleLarge
                )
            },

            text = {
                Text(
                    text = "Are you sure you want to delete this deck?"
                )
            },

            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDeck(deletingDeck.id)
                        deletingDeckId = null
                    }
                ) {
                    Text("OK")
                }
            },

            dismissButton = {
                OutlinedButton(
                    onClick = {
                        deletingDeckId = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    if (movingDeck != null) {
        AlertDialog(
            onDismissRequest = {
                movingDeckId = null
                selectedMoveFolderId = null
            },
            title = {
                Text(
                    text = "Move",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Option to move the deck outside folders
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMoveFolderId == null,
                            onClick = {
                                selectedMoveFolderId = null
                            }
                        )

                        Text(
                            text = "Main library",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // Available folders
                    folders.forEach { folder ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedMoveFolderId == folder.id,
                                onClick = {
                                    selectedMoveFolderId = folder.id
                                }
                            )

                            Text(
                                text = folder.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val deckId = movingDeckId
                        val folderId = selectedMoveFolderId

                        if (deckId != null && folderId != null) {
                            viewModel.moveDeckToFolder(
                                deckId = deckId,
                                folderId = folderId
                            )
                        }

                        movingDeckId = null
                        selectedMoveFolderId = null
                    },
                    enabled = selectedMoveFolderId != null &&
                            selectedMoveFolderId != movingDeck.folderId
                ) {
                    Text("OK")
                }
            },

            dismissButton = {
                OutlinedButton(
                    onClick = {
                        movingDeckId = null
                        selectedMoveFolderId = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DeckRow(
    deck: FlashcardDeck,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit
) {
    var deckOptionsExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = onOpen
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = deck.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = deck.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box {
                IconButton(
                    onClick = {
                        deckOptionsExpanded = true
                    }
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
                            onEdit()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Move") },
                        onClick = {
                            deckOptionsExpanded = false
                            onMove()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            deckOptionsExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddFolderDialog(
    availableDecks: List<FlashcardDeck>,
    editingFolder: FlashcardFolder? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, List<String>) -> Unit
) {
    var name by rememberSaveable(editingFolder?.id) {
        mutableStateOf(editingFolder?.name ?: "")
    }
    var author by rememberSaveable(editingFolder?.id) {
        mutableStateOf(editingFolder?.author ?: "")
    }
    var selectedDeckIds by rememberSaveable(editingFolder?.id) {
        mutableStateOf(
            if (editingFolder != null) {
                availableDecks
                    .filter { deck ->
                        deck.folderId == editingFolder.id
                    }
                    .map { deck -> deck.id }
            } else {
                emptyList<String>()
            }
        )
    }

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
                        text = if (editingFolder == null) {
                            "New folder"
                        } else {
                            "Edit folder"
                        },
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
                    text = "Select the decks to move to the folder.",
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
    editingDeck: FlashcardDeck? = null,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by rememberSaveable(editingDeck?.id) {
        mutableStateOf(editingDeck?.title ?: "")
    }
    var author by rememberSaveable(editingDeck?.id) {
        mutableStateOf(editingDeck?.author ?: "")
    }

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
                        text = if (editingDeck == null) {
                            "Add deck"
                        } else {
                            "Edit deck"
                        },
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
    memorisedCardIds: Set<String>,
    onBack: () -> Unit,
    onAddCard: () -> Unit,
    onDeleteCard: (String) -> Unit,
    onEditCard: (String) -> Unit,
    onMemorise: () -> Unit
) {
    val memorisedCount = flashcards.count { flashcard ->
        flashcard.id in memorisedCardIds
    }
    val progress = if (flashcards.isNotEmpty()) {
        memorisedCount.toFloat() / flashcards.size
    } else {
        0f
    }
    val progressPercentage = (progress * 100).toInt()

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
                                    progress = { progress },
                                    modifier = Modifier.fillMaxSize(),
                                    strokeWidth = 12.dp,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Text(
                                    text = "$progressPercentage%",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            FilledTonalButton(
                                onClick = onMemorise,
                                enabled = flashcards.isNotEmpty()
                            ) {
                                Text("Memorise")
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

@Composable
private fun MemoriseSetupDialog(
    cardCount: Int,
    unmemorisedCardCount: Int,
    onDismiss: () -> Unit,
    onStart: (
        unmemorisedOnly: Boolean,
        randomOrder: Boolean
    ) -> Unit
) {
    var unmemorisedOnly by rememberSaveable { mutableStateOf(true) }
    var randomOrder by rememberSaveable { mutableStateOf(false) }

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Card range 1 ~ $cardCount") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = unmemorisedOnly,
                        onClick = { unmemorisedOnly = true }
                    )
                    Text("Unmemorised cards ($unmemorisedCardCount)")
                }
                Row(verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !unmemorisedOnly,
                        onClick = { unmemorisedOnly = false }
                    )
                    Text("All cards")
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Card order",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = randomOrder,
                        onClick = { randomOrder = true }
                    )
                    Text("Random")
                }
                Row(verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !randomOrder,
                        onClick = { randomOrder = false }
                    )
                    Text("Original")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onStart(
                        unmemorisedOnly,
                        randomOrder
                    )
                },
                enabled = if (unmemorisedOnly) {
                    unmemorisedCardCount > 0
                } else {
                    cardCount > 0
                }
            ) {
                Text("Start")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun MemorisePage(
    flashcards: List<Flashcard>,
    onBack: () -> Unit,
    onFinish: (Map<String, Boolean>) -> Unit
){
    var showHelp by rememberSaveable { mutableStateOf(true) }
    var currentCardIndex by rememberSaveable { mutableIntStateOf(0) }
    var isAnswerRevealed by rememberSaveable { mutableStateOf(false) }
    var knownCount by rememberSaveable { mutableIntStateOf(0) }
    var unknownCount by rememberSaveable { mutableIntStateOf(0) }
    val swipeThreshold = with(LocalDensity.current) { 100.dp.toPx() }
    var swipeHistory by rememberSaveable { mutableStateOf(listOf<Boolean>()) }
    var showResults by rememberSaveable { mutableStateOf(false) }

    val cardCount = flashcards.size
    val currentCard = flashcards.getOrNull(currentCardIndex)

    fun handleSwipe(isKnown: Boolean) {
        if (showHelp || showResults || currentCard == null) return

        swipeHistory = swipeHistory + isKnown

        if (isKnown) {
            knownCount++
        } else {
            unknownCount++
        }

        currentCardIndex++
        isAnswerRevealed = false

        if (currentCardIndex >= cardCount) {
            showResults = true
        }
    }
    fun undoSwipe() {
        if (swipeHistory.isEmpty()) return

        val lastSwipeWasKnown = swipeHistory.last()

        if (lastSwipeWasKnown) {
            knownCount--
        } else {
            unknownCount--
        }

        swipeHistory = swipeHistory.dropLast(1)

        currentCardIndex--
        isAnswerRevealed = false
        showHelp = false
    }

    val cardTypeLabel = when (currentCard?.type) {
        CardType.WORD -> "Word"
        CardType.QUESTION_ANSWER -> "Q&A"
        CardType.TRUE_FALSE -> "True or False"
        CardType.FILL_IN_BLANK -> "Fill in the Blank"
        null -> ""
    }

    if (showResults) {
        MemoriseResultsPage(
            knownCount = knownCount,
            unknownCount = unknownCount,
            onDone = {
                val results = flashcards
                    .take(swipeHistory.size)
                    .mapIndexed { index, flashcard ->
                        flashcard.id to swipeHistory[index]
                    }
                    .toMap()

                onFinish(results)
            }
        )
    } else {
        Column(modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth()
                .height(64.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text("<")
                }
                Text(
                    text = "$currentCardIndex/$cardCount",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✓ $knownCount",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (showHelp) "Help" else cardTypeLabel,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$unknownCount ?",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            // main card
            Card(
                onClick = {
                    if (!showHelp && currentCard != null) {
                        isAnswerRevealed = !isAnswerRevealed
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .pointerInput(showHelp, currentCardIndex, swipeThreshold) {
                        var totalDrag = 0f

                        detectHorizontalDragGestures(
                            onDragStart = {
                                totalDrag = 0f
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                totalDrag += dragAmount
                            },
                            onDragEnd = {
                                if (totalDrag <= -swipeThreshold) {
                                    handleSwipe(isKnown = true)
                                } else if (totalDrag >= swipeThreshold) {
                                    handleSwipe(isKnown = false)
                                }

                                totalDrag = 0f
                            },
                            onDragCancel = {
                                totalDrag = 0f
                            }
                        )
                    },
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()
                    .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (showHelp) {
                        Text(
                            text = "Swipe known cards to the left and unknown cards to the right. Tap the card to reveal its meaning.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else if (currentCard != null) {
                        Text(
                            text = if (isAnswerRevealed) {
                                currentCard.answer
                            } else {
                                currentCard.question
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        showHelp = !showHelp
                        isAnswerRevealed = false
                    }
                ) {
                    Text(if (showHelp) "Continue" else "Help")
                }

                TextButton(
                    onClick = {
                        undoSwipe()
                    },
                    enabled = swipeHistory.isNotEmpty()
                ) {
                    Text("↶ Undo")
                }
            }
        }
    }
}

@Composable
private fun MemoriseResultsPage(
    knownCount: Int,
    unknownCount: Int,
    onDone: () -> Unit
) {
    val totalCards = knownCount + unknownCount
    val accuracy = if (totalCards > 0) {
        knownCount * 100 / totalCards
    } else {
        0
    }

    Column(modifier = Modifier.fillMaxSize()
        .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Memorise results",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Session complete!",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "You have reviewed all $totalCards cards.",
            style = MaterialTheme.typography.bodyLarge
        )
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$knownCount",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text("Known")
                }
            }

            Card(modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$unknownCount",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.error
                    )

                    Text("Unknown")
                }
            }
        }
        Text(
            text = "$accuracy%",
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = "Session accuracy",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done")
        }
    }
}

// preview
@Preview
@Composable
private fun MockFlashcardsPagePreview() {
    MaterialTheme { FlashcardsPage(Modifier, FlashcardsViewModel()) }
}