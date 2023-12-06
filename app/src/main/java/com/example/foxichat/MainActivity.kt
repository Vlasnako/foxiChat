package com.example.foxichat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foxichat.navigation.Screen
import com.example.foxichat.ui.theme.JetpackComposeExTheme
import com.example.foxichat.user_interface.Screens
import com.example.foxichat.view_model.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            val screens = Screens(auth, navController)
            JetpackComposeExTheme {

                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.SIGNUP.name
                    ) {
                        composable(Screen.HOME.name) {
                            screens.ChatScreen()
                        }
                        composable(Screen.SIGNUP.name) {
                            screens.SignUpScreen()
                        }
                    }
//                    val chatViewModel = ChatViewModel()
//                    chatViewModel.runChat()
                }
            }

            val currentUser = auth.currentUser
            if (currentUser != null) {
                navController.navigate(Screen.HOME.name)
            }
            navController.navigate(Screen.SIGNUP.name)
        }

    }
    public override fun onStart() {
       super.onStart()
        auth = Firebase.auth
    }

}









