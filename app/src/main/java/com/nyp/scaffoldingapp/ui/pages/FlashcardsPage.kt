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

    var newOptionsExpanded by rememberSaveable { mutableStateOf(false) }
    var moreOptionsExpanded by rememberSaveable { mutableStateOf(false) }
    var showAddDeckDialog by rememberSaveable { mutableStateOf(false) }
    var showAddFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showArchived by rememberSaveable { mutableStateOf(false) }
    var folderOptionsExpanded by rememberSaveable { mutableStateOf(false) }

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
                        text = { Text("Add flashcards") },
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
            items(items = folders, key = {it.id}) { folder ->
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
                                Spacer(Modifier.width(16.dp))
                                Text(text = deck.title)
                            }
                        }
                    }
                }
            }
        }
    }
}



// preview
@Preview
@Composable
private fun MockFlashcardsPagePreview() {
    MaterialTheme { FlashcardsPage(Modifier, FlashcardsViewModel()) }
}