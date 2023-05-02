package com.example.firebaseapp.utils

data class MovieData (var movieId: String = "", var title: String = "", var director: String = "", var genre: String = "") {
    constructor() : this("", "", "", "")
}