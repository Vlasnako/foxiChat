package com.example.foxichat.entity

data class Message(
    var imageUrl: String? = null,
    val author: String,
    val body: String,
    var isFromMe: Boolean = false,
    val visibleTo: MutableList<User> = mutableListOf()
) {
    constructor() : this(null, "User", "Some message")
}