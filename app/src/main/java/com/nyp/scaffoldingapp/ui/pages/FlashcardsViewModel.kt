package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.UUID

class FlashcardsViewModel : ViewModel() {
    private val deckItems = mutableStateListOf<FlashcardDeck>()
    private val cardItems = mutableStateListOf<StudyCard>()

    val decks: List<FlashcardDeck> get() = deckItems
    val cards: List<StudyCard> get() = cardItems

    fun addDeck(title: String, author: String) {
        deckItems.add(
            FlashcardDeck(
                id = UUID.randomUUID().toString(),
                title = title,
                author = author
            )
        )
    }

    fun addCard(deckId: String, front: String, back: String) {
        cardItems.add(
            StudyCard(
                id = UUID.randomUUID().toString(),
                deckId = deckId,
                front = front,
                back = back
            )
        )
    }

    fun editCard(id: String, front: String, back: String) {
        val index = cardItems.indexOfFirst { it.id == id }
        if (index >= 0) {
            cardItems[index] = cardItems[index].copy(front = front, back = back)
        }
    }

    fun deleteCard(id: String) {
        cardItems.removeAll { it.id == id }
    }

    fun editDeck(id: String, title: String, author: String) {
        val index = deckItems.indexOfFirst { it.id == id }
        if (index >= 0) {
            deckItems[index] = deckItems[index].copy(title = title, author = author)
        }
    }

    fun deleteDeck(id: String) {
        cardItems.removeAll { it.deckId == id }
        deckItems.removeAll { it.id == id }
    }
}