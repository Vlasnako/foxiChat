package com.example.foxichat.view_model

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.foxichat.entity.Message
import com.example.foxichat.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    val messages = mutableStateListOf<Message>()
    val database = FirebaseDatabase.getInstance()
    val messagesRef = database.reference.child("messages")
    fun addNewUser(auth: FirebaseAuth, email: String, password: String, nav: NavHostController, snackbarHostState: SnackbarHostState) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = auth.currentUser
                    nav.navigate(Screen.SIGNIN.name)
                    val coroutineScope = CoroutineScope(Dispatchers.IO)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Success", duration = SnackbarDuration.Short)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    val coroutineScope = CoroutineScope(Dispatchers.IO)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Something went wrong :(", duration = SnackbarDuration.Short)
                    }

                }
            }

    }

    fun signInUser(auth: FirebaseAuth, email: String, password: String, nav: NavHostController, snackbarHostState: SnackbarHostState) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    nav.navigate(Screen.HOME.name)
                } else {
                    // If sign in fails, display a message to the user.
                    val coroutineScope = CoroutineScope(Dispatchers.IO)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Wrong username or password", duration = SnackbarDuration.Short)
                    }
                }
            }
    }
    fun sendMessage(auth: FirebaseAuth, body: String) {
        val message =
            Message(author = auth.currentUser?.email.toString(), body = body)


        messagesRef
            .push()
            .setValue(message)

    }


    fun runChat() {
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.reference.child("messages")

        messagesRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let { messages.add(message) }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

}