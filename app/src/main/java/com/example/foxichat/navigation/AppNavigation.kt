package com.example.foxichat.navigation

import com.example.foxichat.ChatDatabase
import com.example.foxichat.auth.ChatAuth

enum class Screen {
    HOME,
    SIGNUP,
    SIGNIN,
    CHAT_SCREEN
}
    fun startScreen(): String {
        return if (ChatAuth.auth.currentUser == null) Screen.SIGNUP.name else {
            ChatDatabase.initListOfUsers()
            Screen.HOME.name
        }
    }
