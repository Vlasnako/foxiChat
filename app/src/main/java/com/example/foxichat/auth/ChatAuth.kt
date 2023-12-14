package com.example.foxichat.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object ChatAuth {
    lateinit var auth: FirebaseAuth

    fun completeAuth() {
        auth = Firebase.auth
    }

}