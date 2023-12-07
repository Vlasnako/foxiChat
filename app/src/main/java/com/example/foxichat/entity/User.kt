package com.example.foxichat.entity

class User (
    val email: String,
    val userName: String,
    val id: String
) {
    constructor() : this("", "", "")
}