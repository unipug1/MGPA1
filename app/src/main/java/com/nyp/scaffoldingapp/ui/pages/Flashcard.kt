package com.nyp.scaffoldingapp.ui.pages

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