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
import com.example.foxichat.entity.User
import com.example.foxichat.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    companion object {
        private const val MESSAGES_BRANCH = "messages"
        private const val USERS_BRANCH = "users"
    }
    var currentUser: User? = null

    val messages = mutableStateListOf<Message>()
    val users = mutableStateListOf<User>()

    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.reference.child(MESSAGES_BRANCH)
    private val usersRef = database.reference.child(USERS_BRANCH)
    fun addNewUser(auth: FirebaseAuth, email: String, password: String, username: String, nav: NavHostController) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = auth.currentUser
                    createNewUser(user)
                    nav.navigate(Screen.SIGNIN.name)
                    val coroutineScope = CoroutineScope(Dispatchers.IO)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    val coroutineScope = CoroutineScope(Dispatchers.IO)

                }
            }

    }

    private fun createNewUser(firebaseUser: FirebaseUser?) {
        firebaseUser?.let {
            val user = User(email = it.email.toString(), userName = "user", id = it.uid)
            usersRef.push().setValue(user)
        }

    }

    fun signInUser(auth: FirebaseAuth, email: String, password: String, nav: NavHostController) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    initCurrentUser()
                    nav.navigate(Screen.HOME.name)
                } else {
                    // If sign in fails, display a message to the user.

                }
            }
    }
    fun sendMessage(auth: FirebaseAuth, body: String) {


        val message =
            currentUser?.userName?.let { Message(author = it, body = body) }


        messagesRef
            .push()
            .setValue(message)

    }

    private fun initCurrentUser() {
        usersRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                user?.let { users.add(it) }
                Log.d("DATABASE_USERS", users.toString())

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


    fun runChat() {

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