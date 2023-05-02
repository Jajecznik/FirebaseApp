package com.example.firebaseapp.utils

data class GameData (var gameId: String = "", var title: String = "", var developer: String = "", var genre: String = "") {
    constructor() : this("", "", "", "")
}