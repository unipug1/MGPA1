package com.nyp.scaffoldingapp.ui.pages

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class FlashcardsViewModel : ViewModel() {
    private val _decks = MutableStateFlow<List<FlashcardDeck>>(emptyList())
    private val _folders = MutableStateFlow<List<FlashcardFolder>>(emptyList())

    // read only:
    val decks = _decks.asStateFlow()
    val folders = _folders.asStateFlow()

    fun addDeck(title: String, author: String): String {
        val newDeck = FlashcardDeck(
            id = UUID.randomUUID().toString(),
            title = title,
            author = author
        )
        _decks.value += newDeck

        return newDeck.id
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

    fun moveDecksToFolder(deckIds: List<String>, folderId: String) {
        _decks.value = _decks.value.map { deck ->
            if (deck.id in deckIds) {
                deck.copy(folderId = folderId)
            } else {
                deck
            }
        }
    }
}