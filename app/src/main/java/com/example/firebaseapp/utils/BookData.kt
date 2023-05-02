package com.example.firebaseapp.utils

data class BookData (var bookId: String = "", var title: String = "", var author: String = "", var genre: String = "") {
    constructor() : this("", "", "", "")
}