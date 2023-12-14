package com.example.foxichat.entity

data class User (
    val email: String,
    val userName: String,
    val id: String
) {
    constructor() : this("", "", "")
}