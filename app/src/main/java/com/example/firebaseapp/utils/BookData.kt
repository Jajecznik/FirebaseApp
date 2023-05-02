package com.example.firebaseapp.utils

data class BookData (var bookId: String = "", val title: String = "", val author: String = "", val genre: String = "") {
    constructor() : this("", "", "", "")
}