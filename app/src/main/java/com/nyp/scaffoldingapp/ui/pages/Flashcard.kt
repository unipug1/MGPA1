package com.nyp.scaffoldingapp.ui.pages

import androidx.annotation.Nullable

data class FlashcardDeck(
    val id: String,
    val title: String,
    val author: String,
    val folderId: String? = null
)

data class StudyCard(
    val id: String,
    val deckId: String,
    val front: String,
    val back: String,
)

data class FlashcardFolder(
    val id: String,
    val name: String,
    val author: String
)