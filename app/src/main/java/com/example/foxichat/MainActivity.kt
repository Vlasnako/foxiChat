package com.example.foxichat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foxichat.auth.ChatAuth
import com.example.foxichat.navigation.Screen
import com.example.foxichat.navigation.startScreen
import com.example.foxichat.ui.theme.JetpackComposeExTheme
import com.example.foxichat.user_interface.Screens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ChatAuth.completeAuth()
        setContent {
            JetpackComposeExTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                        NavigationHost()
                    }
                }
            }
    }
    public override fun onStart() {
       super.onStart()
    }
    @Composable
    fun NavigationHost() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = startScreen()
        ) {

            val screens = Screens(navController)
            composable(Screen.HOME.name) {
                screens.HomeScreen()
            }
            composable(Screen.SIGNUP.name) {
                screens.SignUpScreen()
            }
            composable(Screen.SIGNIN.name) {
                screens.SignInScreen()
            }
            composable(Screen.CHAT_SCREEN.name) {
                screens.ChatScreen()
            }
        }
    }
}









