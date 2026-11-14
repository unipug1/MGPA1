package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.items
import java.util.UUID
import androidx.activity.compose.BackHandler

@Composable
fun FlashcardsPage(modifier: Modifier = Modifier,
                   onBack: () -> Unit = {}
) {
    var newOptionsExpanded by rememberSaveable {mutableStateOf(false)}
    var moreOptionsExpanded by rememberSaveable {mutableStateOf(false)}

    val decks = remember { mutableStateListOf<FlashcardDeck>() }
    var showAddDeckDialog by rememberSaveable {mutableStateOf(false)}

    var selectedDeckId by rememberSaveable {mutableStateOf<String?>(null)}
    val selectedDeck = decks.firstOrNull{it.id == selectedDeckId}

    val cards = remember {mutableStateListOf<StudyCard>()}

    if (selectedDeck != null) {
        DeckDetailPage(
            deck = selectedDeck,
            cards = cards.filter {it.deckId == selectedDeck.id},
            onBack = {selectedDeckId = null},
            onAddCard = { front, back ->
                cards.add(
                    StudyCard(
                        id = UUID.randomUUID().toString(),
                        deckId = selectedDeck.id,
                        front = front,
                        back = back
                    )
                )
            },
            onEditCard = { id, front, back ->
                val index = cards.indexOfFirst { it.id == id }
                if (index >= 0) {
                    cards[index] = cards[index].copy(front = front, back = back)
                }
            }
        )
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column() {
            Row(Modifier.fillMaxWidth()) {
                Button(onClick = onBack) {
                    Text("<")
                }
                Spacer(Modifier.weight(1f))
                Text("Flash cards")
            }


            Row(Modifier.fillMaxWidth()) {
                Box {
                    Button(onClick = { newOptionsExpanded = true }) {
                        Text("+ New")
                    }


                    DropdownMenu(
                        expanded = newOptionsExpanded,
                        onDismissRequest = { newOptionsExpanded = false }
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
                            onClick = { newOptionsExpanded = false }
                        )
                    }
                }


                Box {
                    Button(onClick = { moreOptionsExpanded = true }) {
                        Text("···")
                    }


                    DropdownMenu(
                        expanded = moreOptionsExpanded,
                        onDismissRequest = { moreOptionsExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort by") },
                            onClick = { moreOptionsExpanded = false }
                        )


                        DropdownMenuItem(
                            text = { Text("Archived") },
                            onClick = { moreOptionsExpanded = false }
                        )


                        DropdownMenuItem(
                            text = { Text("List") },
                            onClick = { moreOptionsExpanded = false }
                        )
                    }
                }
            }


            LazyColumn(Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp))
            {
                items(decks, key = {it.id}) { deck ->
                    Card(onClick = {selectedDeckId = deck.id},
                        Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(deck.title)


                            if (deck.author.isNotBlank()) {
                                Text(deck.author)
                            }
                        }
                    }
                }
            }
        }
    }


    if (showAddDeckDialog) {
        AddFlashcardPageDialog(
            onDismiss = {showAddDeckDialog = false},
            onSave = {title, author ->
                decks.add(
                    FlashcardDeck(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        author = author
                    )
                )
                showAddDeckDialog = false
            }
        )
    }
}


@Composable
private fun AddFlashcardPageDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, author: String) -> Unit
) {
    var title by rememberSaveable {
        mutableStateOf("")
    }
    var author by rememberSaveable {
        mutableStateOf("")
    }


    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onDismiss) {
                    Text("✗")
                }
                Spacer(Modifier.weight(1f))
                Text("Add flashcards")
                Spacer(Modifier.weight(1f))
                Button(onClick = {
                    onSave(title.trim(), author.trim())
                },
                    enabled = title.isNotBlank()) {
                    Text("✓")
                }
            }


            Card() {
                OutlinedTextField(
                    value = title,
                    onValueChange = {title = it},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("title") }
                )
                HorizontalDivider(thickness = 2.dp)
                OutlinedTextField(
                    value = author,
                    onValueChange = {author = it},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Author") }
                )
            }


            Spacer(Modifier.height(8.dp))


            // color wheel
            Card() {
                Row(Modifier.fillMaxWidth()) {
                    Text("Cover color")
                    Spacer(Modifier.weight(1f))
                    Button(onClick = {}) {
                        Text(">")
                    }
                }
            }
        }
    }
}


@Composable
private fun AddFolderDialog(
    onDismiss: () -> Unit
) {
    var folderName by rememberSaveable {
        mutableStateOf("")
    }
    var authorName by rememberSaveable {
        mutableStateOf("")
    }


    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onDismiss) {
                    Text("✗")
                }
                Spacer(Modifier.weight(1f))
                Text("New folder")
                Spacer(Modifier.weight(1f))
                Button(onClick = {},
                    enabled = folderName.isNotBlank()
                ) {
                    Text("✓")
                }
            }


            Card() {
                OutlinedTextField(
                    value = folderName,
                    onValueChange = {folderName = it},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter folder name.") }
                )
            }


            Spacer(Modifier.height(8.dp))


            Card() {
                OutlinedTextField(
                    value = authorName,
                    onValueChange = {authorName = it},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Author") }
                )
            }


            Spacer(Modifier.height(8.dp))


            Text("Select the flashcards to move to the folder.")


            // lazy list to render flashcards
        }
    }
}


@Composable
private fun DeckDetailPage(
    deck: FlashcardDeck,
    cards: List<StudyCard>,
    onBack: () -> Unit,
    onAddCard: (front: String, back: String) -> Unit,
    onEditCard: (id: String, front: String, back:String) -> Unit
) {
    var showCardEditor by rememberSaveable {mutableStateOf(false)}
    var editingCardId by rememberSaveable {mutableStateOf<String?>(null)}
    val editingCard = cards.firstOrNull{it.id == editingCardId}

    if (showCardEditor) {
        CardEditorPage(
            onBack = {showCardEditor = false},
            onSave = {front, back ->
                if (editingCard != null) {
                    onEditCard(editingCard.id, front, back)
                } else {
                    onAddCard(front, back)
                }

                showCardEditor = false
                editingCardId = null
            }
        )
        return
    }

    BackHandler(onBack = onBack)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onBack) {
                Text("<")
            }


            Text(deck.title, style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(16.dp))
        Text("Cards ${cards.size}")

        LazyColumn(Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp))
        {
            if (cards.isEmpty()) {
                item {Text("Tap the + button to add the flashcards")}
            }

            items(cards, key = {it.id}) {card ->
                Card(onClick = {
                        editingCardId = card.id
                        showCardEditor = true
                    },
                    Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(card.front)
                        Text(card.back)
                    }
                }
            }
        }

        ElevatedButton(
            onClick = {
                editingCardId = null
                showCardEditor = true
            }
        ) {
            Text("+")
        }
    }
}


@Composable
private fun CardEditorPage(
    onBack: () -> Unit,
    onSave: (front: String, back: String) -> Unit,
    card: StudyCard? = null
)
{
    var front by rememberSaveable(card?.id) {
        mutableStateOf(card?.front.orEmpty())
    }
    var back by rememberSaveable(card?.id) {
        mutableStateOf(card?.back.orEmpty())
    }

    BackHandler(onBack = onBack)

    Column(Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp))
    {
        Text(text = if (card == null) "Add card" else "Edit card",
            style = MaterialTheme.typography.titleLarge
        )
        OutlinedTextField(
            value = front,
            onValueChange = { front = it },
            label = { Text("Question") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = back,
            onValueChange = { back = it },
            label = { Text("Answer") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onBack) {
                Text("Cancel")
            }
            Button(
                onClick = { onSave(front.trim(), back.trim()) },
                enabled = front.isNotBlank() && back.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }
}


@Composable
private fun CardView() {
    Text("Memorise")
}


@Composable
private fun StartTest() {
    Text("Test")
}


// preview
@Preview
@Composable
private fun MockFlashcardsPagePreview() {
    MaterialTheme { FlashcardsPage(Modifier) }
}

