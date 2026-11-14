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
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun FlashcardsPage(modifier: Modifier = Modifier,
                   viewModel: FlashcardsViewModel,
                   onBack: () -> Unit = {}
) {
    var newOptionsExpanded by rememberSaveable {mutableStateOf(false)}
    var moreOptionsExpanded by rememberSaveable {mutableStateOf(false)}

    val decks = viewModel.decks
    val cards = viewModel.cards

    var showAddDeckDialog by rememberSaveable {mutableStateOf(false)}

    var selectedDeckId by rememberSaveable {mutableStateOf<String?>(null)}
    val selectedDeck = decks.firstOrNull{it.id == selectedDeckId}

    if (selectedDeck != null) {
        DeckDetailPage(
            deck = selectedDeck,
            cards = cards.filter { it.deckId == selectedDeck.id },
            onBack = { selectedDeckId = null },
            onAddCard = { front, back ->
                viewModel.addCard(selectedDeck.id, front, back)
            },
            onEditCard = { id, front, back ->
                viewModel.editCard(id, front, back)
            },
            onDeleteCard = { id ->
                viewModel.deleteCard(id)
            },
            onEditDeck = { title, author ->
                viewModel.editDeck(selectedDeck.id, title, author)
            },
            onDeleteDeck = {
                viewModel.deleteDeck(selectedDeck.id)
                selectedDeckId = null
            },
            isMemorised = viewModel::isMemorised,
            onReviewDone = viewModel::applyReview
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
            onSave = { title, author ->
                viewModel.addDeck(title, author)
                showAddDeckDialog = false
            }
        )
    }
}


@Composable
private fun AddFlashcardPageDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, author: String) -> Unit,
    deck: FlashcardDeck? = null
) {
    var title by rememberSaveable(deck?.id) {
        mutableStateOf(deck?.title.orEmpty())
    }
    var author by rememberSaveable(deck?.id) {
        mutableStateOf(deck?.author.orEmpty())
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
                Text(if (deck == null) "Add flashcards" else "Edit deck")
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
private fun DeckDetailPage(
    deck: FlashcardDeck,
    cards: List<StudyCard>,
    onBack: () -> Unit,
    onAddCard: (front: String, back: String) -> Unit,
    onEditCard: (id: String, front: String, back:String) -> Unit,
    onDeleteCard: (id: String) -> Unit,
    onEditDeck: (title: String, author: String) -> Unit,
    onDeleteDeck: () -> Unit,
    isMemorised: (String) -> Boolean,
    onReviewDone: (Map<String, Boolean>) -> Unit
) {
    var showCardEditor by rememberSaveable {mutableStateOf(false)}
    var editingCardId by rememberSaveable {mutableStateOf<String?>(null)}
    val editingCard = cards.firstOrNull{it.id == editingCardId}
    var deckMenuExpanded by remember { mutableStateOf(false) }
    var showEditDeckDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDeckDialog by rememberSaveable { mutableStateOf(false) }

    // memorise
    var showMemoriseSetup by rememberSaveable {mutableStateOf(false)}
    var unmemorisedOnly by rememberSaveable {mutableStateOf(false)}
    var randomOrder by rememberSaveable {mutableStateOf(false)}
    var swapSides by rememberSaveable {mutableStateOf(false)}

    var useTimer by rememberSaveable { mutableStateOf(false) }
    var secondsPerCard by rememberSaveable { mutableStateOf(15) }
    var timerOptionsExpanded by remember { mutableStateOf(false) }

    var reviewCards by remember {mutableStateOf<List<StudyCard>>(emptyList())}

    val unmemorisedCards = cards.filterNot{isMemorised(it.id)}
    val eligibleCards = if (unmemorisedOnly) unmemorisedCards else cards

    if (showCardEditor) {
        CardEditorPage(
            card = editingCard,
            onBack = {
                showCardEditor = false
                editingCardId = null
            },
            onSave = {front, back ->
                if (editingCard != null) {
                    onEditCard(editingCard.id, front, back)
                } else {
                    onAddCard(front, back)
                }

                showCardEditor = false
                editingCardId = null
            },
            onDelete = {
                editingCard?.let {onDeleteCard(it.id)}
                showCardEditor = false
                editingCardId = null
            }
        )
        return
    }

    if (reviewCards.isNotEmpty()) {
        CardView(
            cards = reviewCards,
            swapSides = swapSides,
            secondsPerCard = if (useTimer) secondsPerCard else null,
            onBack = { reviewCards = emptyList() },
            onDone = { results ->
                onReviewDone(results)
                reviewCards = emptyList()
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
            Text(deck.title, style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f))
            Box {
                TextButton(onClick = { deckMenuExpanded = true }) {
                    Text("⋮")
                }

                DropdownMenu(
                    expanded = deckMenuExpanded,
                    onDismissRequest = { deckMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            deckMenuExpanded = false
                            showEditDeckDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            deckMenuExpanded = false
                            showDeleteDeckDialog = true
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { showMemoriseSetup = true },
            enabled = cards.isNotEmpty()
        ) {
            Text("Memorise")
        }

        Text("Memorised ${cards.size - unmemorisedCards.size} / ${cards.size}")
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

    if (showEditDeckDialog) {
        AddFlashcardPageDialog(
            deck = deck,
            onDismiss = { showEditDeckDialog = false },
            onSave = { title, author ->
                onEditDeck(title, author)
                showEditDeckDialog = false
            }
        )
    }

    if (showDeleteDeckDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDeckDialog = false },
            title = { Text("Delete deck?") },
            text = { Text("Delete “${deck.title}” and all its cards?") },
            dismissButton = {
                TextButton(onClick = { showDeleteDeckDialog = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDeckDialog = false
                    onDeleteDeck()
                }) {
                    Text("Delete")
                }
            }
        )
    }

    if (showMemoriseSetup) {
        AlertDialog(
            onDismissRequest = { showMemoriseSetup = false },
            title = { Text("Memorise") },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = !unmemorisedOnly,
                            onClick = { unmemorisedOnly = false }
                        )
                        Text("All cards (${cards.size})")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = unmemorisedOnly,
                            onClick = { unmemorisedOnly = true }
                        )
                        Text("Unmemorised cards (${unmemorisedCards.size})")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = !randomOrder,
                            onClick = { randomOrder = false }
                        )
                        Text("Original order")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = randomOrder,
                            onClick = { randomOrder = true }
                        )
                        Text("Random order")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = swapSides,
                            onCheckedChange = { swapSides = it }
                        )
                        Text("Swap front and back")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Use timer")
                        Switch(
                            checked = useTimer,
                            onCheckedChange = {useTimer = it}
                        )
                    }
                    if (useTimer) {
                        Box {
                            OutlinedButton(onClick = { timerOptionsExpanded = true }) {
                                Text("$secondsPerCard seconds per card")
                            }

                            DropdownMenu(
                                expanded = timerOptionsExpanded,
                                onDismissRequest = { timerOptionsExpanded = false }
                            ) {
                                listOf(5, 10, 15, 30).forEach { seconds ->
                                    DropdownMenuItem(
                                        text = { Text("$seconds seconds") },
                                        onClick = {
                                            secondsPerCard = seconds
                                            timerOptionsExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showMemoriseSetup = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(
                    enabled = eligibleCards.isNotEmpty(),
                    onClick = {
                        reviewCards = if (randomOrder) {
                            eligibleCards.shuffled()
                        } else {
                            eligibleCards.toList()
                        }
                        showMemoriseSetup = false
                    }
                ) {
                    Text("Card view")
                }
            }
        )
    }
}


@Composable
private fun CardEditorPage(
    onBack: () -> Unit,
    onSave: (front: String, back: String) -> Unit,
    card: StudyCard? = null,
    onDelete: () -> Unit
)
{
    var front by rememberSaveable(card?.id) {
        mutableStateOf(card?.front.orEmpty())
    }
    var back by rememberSaveable(card?.id) {
        mutableStateOf(card?.back.orEmpty())
    }
    var showDeleteDialog by rememberSaveable(card?.id) {
        mutableStateOf(false)
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
            if (card != null) {
                TextButton(onClick = { showDeleteDialog = true }) {
                    Text("Delete")
                }
            }
        }
    }

    if (card != null && showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete card?") },
            text = { Text("Remove this card from the deck?") },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("Delete")
                }
            }
        )
    }
}


@Composable
private fun CardView(
    cards: List<StudyCard>,
    swapSides: Boolean,
    secondsPerCard: Int?,
    onBack: () -> Unit,
    onDone: (Map<String, Boolean>) -> Unit
)
{
    val decisions = remember { mutableStateListOf<Boolean>() }
    val currentCard = cards.getOrNull(decisions.size)

    var showAnswer by remember(currentCard?.id) { mutableStateOf(false) }
    var hasRevealedAnswer by remember(currentCard?.id) { mutableStateOf(false) }
    var secondsLeft by remember(currentCard?.id, secondsPerCard) {
        mutableStateOf(secondsPerCard ?: 0)
    }

    LaunchedEffect(currentCard?.id, secondsPerCard, hasRevealedAnswer) {
        if (currentCard != null && secondsPerCard != null && !hasRevealedAnswer) {
            while (secondsLeft > 0) {
                delay(1_000L)
                if (hasRevealedAnswer) break
                secondsLeft--
            }

            if (secondsLeft == 0 && !hasRevealedAnswer) {
                showAnswer = true
                hasRevealedAnswer = true
            }
        }
    }

    BackHandler(onBack = onBack)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    )
    {
        Button(onClick = onBack) {
            Text("Back")
        }

        Text("${decisions.size} / ${cards.size} reviewed")

        if (currentCard != null) {
            val prompt = if (swapSides) currentCard.back else currentCard.front
            val answer = if (swapSides) currentCard.front else currentCard.back

            if (secondsPerCard != null) {
                Text(
                    when {
                        secondsLeft == 0 -> "Time’s up"
                        hasRevealedAnswer -> "Timer stopped"
                        else -> "$secondsLeft seconds left"
                    }
                )
            }

            Card(
                onClick = { showAnswer = !showAnswer
                    if (showAnswer) {
                        hasRevealedAnswer = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (showAnswer) answer else prompt,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { decisions.add(true) }) {
                    Text("Known")
                }
                Button(onClick = { decisions.add(false) }) {
                    Text("Still learning")
                }
            }
        } else {
            Text("Known cards: ${decisions.count { it }}")
            Text("Still learning cards: ${decisions.count { !it }}")

            Button(onClick = {
                onDone(cards.map { it.id }.zip(decisions).toMap())
            }) {
                Text("Done")
            }
        }

        TextButton(
            enabled = decisions.isNotEmpty(),
            onClick = { decisions.removeAt(decisions.lastIndex) }
        ) {
            Text("Undo")
        }
    }
}

// preview
@Preview
@Composable
private fun MockFlashcardsPagePreview() {
    MaterialTheme { FlashcardsPage(Modifier, FlashcardsViewModel()) }
}

