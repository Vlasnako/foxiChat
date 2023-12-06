package com.example.foxichat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foxichat.navigation.Screen
import com.example.foxichat.ui.theme.JetpackComposeExTheme
import com.example.foxichat.user_interface.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            JetpackComposeExTheme {

                Surface(modifier = Modifier.fillMaxSize()) {
                    val snackbarHostState = remember{ SnackbarHostState() }
//                    Scaffold(
//                        snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
//                    ) {
  //                      Box(
 //                           modifier = Modifier.padding(it).fillMaxSize()
 //                       ) {
                           NavigationHost(snackbarHostState)
//                        }
                    }
                }
            }


        }

    //}
    public override fun onStart() {
       super.onStart()
        auth = Firebase.auth
    }
    @Composable
    fun NavigationHost(snackbarHostState: SnackbarHostState) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = if (auth.currentUser == null) Screen.SIGNUP.name else Screen.HOME.name
        ) {

            val screens = Screens(auth, navController, snackbarHostState)
            composable(Screen.HOME.name) {
                screens.ChatScreen()
            }
            composable(Screen.SIGNUP.name) {
                screens.SignUpScreen()
            }
            composable(Screen.SIGNIN.name) {
                screens.SignInScreen()
            }
        }
    }


}









