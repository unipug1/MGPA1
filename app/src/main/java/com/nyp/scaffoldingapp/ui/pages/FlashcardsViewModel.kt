package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.UUID
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf

class FlashcardsViewModel : ViewModel() {
    private val deckItems = mutableStateListOf<FlashcardDeck>()
    private val cardItems = mutableStateListOf<StudyCard>()
    private val memorisedById = mutableStateMapOf<String, Boolean>()
    private val folderItems = mutableStateListOf<FlashcardFolder>()

    val decks: List<FlashcardDeck> get() = deckItems
    val cards: List<StudyCard> get() = cardItems
    val folders: List<FlashcardFolder> get() = folderItems

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
        memorisedById.remove(id)
        cardItems.removeAll { it.id == id }
    }

    fun editDeck(id: String, title: String, author: String) {
        val index = deckItems.indexOfFirst { it.id == id }
        if (index >= 0) {
            deckItems[index] = deckItems[index].copy(title = title, author = author)
        }
    }

    fun deleteDeck(id: String) {
        cardItems.filter { it.deckId == id }.forEach { card ->
            memorisedById.remove(card.id)
        }

        cardItems.removeAll { it.deckId == id }
        deckItems.removeAll { it.id == id }
    }

    fun isMemorised(cardId: String): Boolean = memorisedById[cardId] == true
    fun applyReview(results: Map<String, Boolean>) {
        memorisedById.putAll(results)
    }

    fun addFolder(name: String, author: String) {
        folderItems.add(
            FlashcardFolder(
                id = UUID.randomUUID().toString(),
                name = name,
                author = author
            )
        )
    }
}