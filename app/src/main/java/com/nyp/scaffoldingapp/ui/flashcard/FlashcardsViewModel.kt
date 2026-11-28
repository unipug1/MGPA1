package com.nyp.scaffoldingapp.ui.flashcard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class FlashcardsViewModel : ViewModel() {
    private val _decks = MutableStateFlow<List<FlashcardDeck>>(emptyList())
    private val _folders = MutableStateFlow<List<FlashcardFolder>>(emptyList())
    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    private val _memorisedCardIds = MutableStateFlow<Set<String>>(emptySet())

    // read only:
    val decks = _decks.asStateFlow()
    val folders = _folders.asStateFlow()
    val flashcards = _flashcards.asStateFlow()
    val memorisedCardIds = _memorisedCardIds.asStateFlow()

    fun addDeck(title: String, author: String): String {
        val newDeck = FlashcardDeck(
            id = UUID.randomUUID().toString(),
            title = title,
            author = author
        )
        _decks.value += newDeck

        return newDeck.id
    }

    fun updateDeck(deckId: String, title: String, author: String) {
        _decks.value = _decks.value.map { deck ->
            if (deck.id == deckId) {
                deck.copy(
                    title = title,
                    author = author
                )
            } else {
                deck
            }
        }
    }

    fun deleteDeck(deckId: String) {
        // Get the IDs of flashcards belonging to this deck
        val flashcardIds = _flashcards.value
            .filter { flashcard ->
                flashcard.deckId == deckId
            }
            .map { flashcard ->
                flashcard.id
            }
            .toSet()

        // Delete the deck
        _decks.value = _decks.value.filter { deck ->
            deck.id != deckId
        }

        // Delete its flashcards
        _flashcards.value = _flashcards.value.filter { flashcard ->
            flashcard.deckId != deckId
        }

        // Remove memorisation progress for deleted flashcards
        _memorisedCardIds.value -= flashcardIds
    }

    fun addFolder(name: String, author: String): String {
        val newFolder = FlashcardFolder(
            id = UUID.randomUUID().toString(),
            name = name,
            author = author
        )
        _folders.value += newFolder

        return newFolder.id
    }

    fun updateFolder(folderId: String, name: String, author: String) {
        _folders.value = _folders.value.map { folder ->
            if (folder.id == folderId) {
                folder.copy(
                    name = name,
                    author = author
                )
            } else {
                folder
            }
        }
    }

    fun updateFolderDecks(folderId: String, selectedDeckIds: List<String>) {
        _decks.value = _decks.value.map { deck ->
            when {
                deck.id in selectedDeckIds -> {
                    deck.copy(folderId = folderId)
                }

                deck.folderId == folderId -> {
                    deck.copy(folderId = null)
                }

                else -> deck
            }
        }
    }

    fun moveDecksToFolder(deckIds: List<String>, folderId: String) {
        _decks.value = _decks.value.map { deck ->
            if (deck.id in deckIds) {
                deck.copy(folderId = folderId)
            } else {
                deck
            }
        }
    }

    fun moveDeckToFolder(deckId: String, folderId: String?) {
        _decks.value = _decks.value.map { deck ->
            if (deck.id == deckId) {
                deck.copy(folderId = folderId)
            } else {
                deck
            }
        }
    }

    fun deleteFolder(folderId: String) {
        // Remove the folder
        _folders.value = _folders.value.filter { folder ->
            folder.id != folderId
        }

        // Move its decks back to the main library
        _decks.value = _decks.value.map { deck ->
            if (deck.folderId == folderId) {
                deck.copy(folderId = null)
            } else {
                deck
            }
        }
    }

    fun addFlashcard(deckId: String, type: CardType, question: String, answer: String) {
        val newFlashcard = Flashcard(
            id = UUID.randomUUID().toString(),
            deckId = deckId,
            type = type,
            question = question,
            answer = answer
        )
        _flashcards.value += newFlashcard
    }

    fun deleteFlashcard(flashcardId: String) {
        _flashcards.value = _flashcards.value.filter { flashcard ->
            flashcard.id != flashcardId
        }

        _memorisedCardIds.value -= flashcardId
    }

    fun updateFlashcard(flashcardId: String, type: CardType, question: String, answer: String) {
        _flashcards.value = _flashcards.value.map { flashcard ->
            if (flashcard.id == flashcardId) {
                flashcard.copy(
                    type = type,
                    question = question,
                    answer = answer
                )
            } else {
                flashcard
            }
        }
    }

    fun setCardMemorised(flashcardId: String, isMemorised: Boolean
    ) {
        _memorisedCardIds.value = if (isMemorised) {
            _memorisedCardIds.value + flashcardId
        } else {
            _memorisedCardIds.value - flashcardId
        }
    }
}