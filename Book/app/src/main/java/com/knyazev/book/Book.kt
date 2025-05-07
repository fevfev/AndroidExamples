package com.knyazev.book

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val shortDescription: String,
    val fullDescription: String,
    val imageRes: Int,
    var isFavorite: Boolean = false
)