package com.nyp.scaffoldingapp.ui.flashcard

data class FlashcardDeck(
    val id: String,
    val title: String,
    val author: String,
    val folderId: String? = null
)

data class FlashcardFolder(
    val id: String,
    val name: String,
    val author: String
)

enum class CardType{
    WORD,
    QUESTION_ANSWER,
    TRUE_FALSE,
    FILL_IN_BLANK
}

data class Flashcard(
    val id: String,
    val deckId: String,
    val type: CardType,
    val question: String,
    val answer: String
)