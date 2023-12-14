package com.example.foxichat.view_model
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.foxichat.ChatDatabase
import com.example.foxichat.auth.ChatAuth.auth
import com.example.foxichat.entity.Message
import com.example.foxichat.entity.User
import com.example.foxichat.navigation.Screen

class ChatViewModel : ViewModel() {

    private var isHomeScreenFirstOpened = true
    private val loadedChats = mutableListOf<String>()

    companion object {
        var currentUser: User? = null
    }
    fun signInUser(email: String, password: String, nav: NavHostController) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    initUsersFromDb()
                    nav.navigate(Screen.HOME.name)

                } else {


                }
            }
    }
    fun getMessages(otherUser: User): SnapshotStateList<Message>? {
        if (auth.currentUser != null) {
            if(!loadedChats.contains(getChatId(auth.currentUser!!.uid, otherUser.id))) {
                initMessagesFromDb(otherUser)
                loadedChats.add(getChatId(auth.currentUser!!.uid, otherUser.id))
            }
            return ChatDatabase.chatMessages[auth.currentUser?.let { getChatId(it.uid, otherUser.id) }]
        }
        return mutableStateListOf()
    }

    fun signOut(nav: NavHostController) {
        auth.signOut()
        nav.navigate(Screen.SIGNUP.name)
    }
    fun getChatId(id1: String, id2: String): String {
        return if (id1.hashCode() < id2.hashCode()) {
            id1+"_"+id2
        } else {
            id2+"_"+id1
        }
    }
    fun addNewUser(nav: NavHostController, email: String, password: String, username: String) {
        ChatDatabase.addUser(email, username, password)
        nav.navigate(Screen.SIGNIN.name)
    }

    fun sendMessage(otherUser: User, text: String) {
        if (auth.currentUser != null) {
            val chatId = getChatId(auth.currentUser!!.uid, otherUser.id)
            ChatDatabase.addMessage(otherUser, text, chatId)
        }

    }
    fun initUsersFromDb() {
     //   if (isHomeScreenFirstOpened) {
            ChatDatabase.initListOfUsers()
      //      isHomeScreenFirstOpened = false
      //  }
    }
    fun initMessagesFromDb(otherUser: User) {
        auth.currentUser?.let { getChatId(it.uid, otherUser.id) }
            ?.let { ChatDatabase.initMessagesList(it) }
    }

    fun getOtherUsers(): SnapshotStateList<User> {
        return ChatDatabase.users
    }


}