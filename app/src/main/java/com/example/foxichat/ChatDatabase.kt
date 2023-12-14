package com.example.foxichat

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.foxichat.auth.ChatAuth
import com.example.foxichat.entity.Message
import com.example.foxichat.entity.User
import com.example.foxichat.view_model.ChatViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

object ChatDatabase {

    private const val MESSAGES_BRANCH = "messages"
    private const val USERS_BRANCH = "users"

    //val messages = mutableStateListOf<Message>()
    val users = mutableStateListOf<User>()
    val chatMessages = mutableStateMapOf<String, SnapshotStateList<Message>>()

    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.reference.child(MESSAGES_BRANCH)
    private val usersRef = database.reference.child(USERS_BRANCH)
    fun addUser(email: String, username: String, password: String) {
        ChatAuth.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = ChatAuth.auth.currentUser
                    createNewUser(user, username)
                } else {


                }
            }
    }

    fun addMessage(otherUser: User, body: String, chatId: String) {
        val chatRef = messagesRef.child(chatId)
        val message =
            ChatViewModel.currentUser?.userName?.let { Message(author = it, body = body) }


        chatRef
            .push()
            .setValue(message)

    }

    private fun createNewUser(firebaseUser: FirebaseUser?, username: String) {
        firebaseUser?.let {
            val user = User(email = it.email.toString(), userName = username, id = it.uid)
            usersRef.push().setValue(user)
        }
    }

    fun initListOfUsers() {
        usersRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                if (user?.id != ChatAuth.auth.currentUser?.uid) {
                    if (!users.contains(user)) {
                        user?.let { users.add(it) }
                    }
                    Log.d("DATABASE_USERS", users.toString())
                } else {
                    ChatViewModel.currentUser = user
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                user?.let { users.remove(it) }
                Log.d("DATABASE_USERS", users.toString())
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                clearUserList()
            }

        })
    }

    fun initMessagesList(chatId: String) {
        val chatRef = messagesRef.child(chatId)
        if (chatMessages[chatId] == null) {
            chatMessages[chatId] = mutableStateListOf()
        }
        chatRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)

                message.let {

                    chatMessages[chatId]?.add(it!!)
                }


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                clearMessageList()
            }


        })

    }

    fun clearUserList() {
        users.clear()
    }

    fun clearMessageList() {

        chatMessages.clear()
    }
}