package com.example.foxichat.user_interface

import android.text.TextUtils
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.foxichat.R
import com.example.foxichat.entity.Message
import com.example.foxichat.view_model.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Screens(val auth: FirebaseAuth, val nav: NavHostController) {
    var viewModel = ChatViewModel()

    @Composable
    fun ChatScreen() {


        viewModel.runChat()

        val messages = remember {
            viewModel.messages
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 45.dp)
        ) {
            items(messages) {
                MessageCard(msg = it)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            var chatBoxValue by remember { mutableStateOf(TextFieldValue("")) }
            Row {
                TextField(
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.fillMaxWidth(),
                    value = chatBoxValue,
                    onValueChange = { chatBoxValue = it },
                    placeholder = {
                        Text(text = "Type something")
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "clear text",
                            modifier = Modifier
                                .clickable {
                                    val message =
                                        Message(author = "Dasha", body = chatBoxValue.text)
                                    message.isFromMe = true
                                    val database = FirebaseDatabase.getInstance()
                                    val messagesRef = database.reference.child("messages")
                                    messagesRef
                                        .push()
                                        .setValue(message)
                                    chatBoxValue = TextFieldValue("")
                                }
                                .padding(end = 10.dp)
                        )
                    }
                )

            }
        }
    }

    @Composable
    fun MessageCard(msg: Message) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = if (msg.isFromMe) Arrangement.End else Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(end = 10.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                var isExpanded by remember {
                    mutableStateOf(false)
                }
                val surfaceColor by animateColorAsState(
                    if (isExpanded) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface, label = ""
                )

                Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                    Text(
                        text = msg.author,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        shadowElevation = 1.dp,
                        color = surfaceColor,
                        modifier = Modifier
                            .animateContentSize()
                            .padding(1.dp)
                    ) {
                        Text(
                            text = msg.body,
                            modifier = Modifier.padding(all = 4.dp),
                            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    @Composable

    fun SignUpScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 30.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row {
                    Image(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(200.dp))
                            .padding(end = 10.dp),
                        contentScale = ContentScale.Crop,

                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "logo"
                    )
                    Text(
                        modifier = Modifier.padding(top = 30.dp),
                        text = "FoxiChat",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                var usernameValue by remember { mutableStateOf(TextFieldValue("")) }
                var isValidUsername by remember { mutableStateOf(true) }
                fun validateUsername(s: String) {
                    if (s.isBlank() || s.contains(' ')) {
                        isValidUsername = false;
                    } else {
                        isValidUsername= android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches();
                    }
                }
                TextField(
                    singleLine = true,
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 180.dp),
                    value = usernameValue,
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    onValueChange = {
                        usernameValue = it
                        validateUsername(usernameValue.text)
                                    },
                    placeholder = {
                        Text(text = "Email")
                    },
                    supportingText = {
                        if (!isValidUsername) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Email must be valid",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                usernameValue = TextFieldValue("")
                            }
                        ) {
                            Icon(
                                Icons.Outlined.Clear,
                                contentDescription = "clear text",
                            )
                        }
                    }
                )
                var passwordValue by remember { mutableStateOf(TextFieldValue("")) }
                var isPasswordVisible by remember { mutableStateOf(false) }
                var isPasswordValid by remember { mutableStateOf(true) }
                TextField(
                    singleLine = true,
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    value = passwordValue,
                    onValueChange = {
                        if (it.text.contains(' ')) {
                            isPasswordValid = false
                            return@TextField
                        }
                        passwordValue = it
                        isPasswordValid = true

                                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),supportingText = {
                        if (!isPasswordValid) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Password must not contain spaces",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    placeholder = {
                        Text(text = "Password")
                    },
                    trailingIcon = {
                        if (isPasswordValid) {
                            IconButton(
                                onClick = {
                                    isPasswordVisible = !isPasswordVisible
                                }
                            ) {
                                Icon(
                                    Icons.Outlined.Face,
                                    contentDescription = "clear text",
                                )
                            }
                        } else {
                            Icon(Icons.Filled.Info,"error", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )
                Button(
                    onClick = {
                        val email = usernameValue.text.trim()
                        val password = passwordValue.text
                        viewModel.addNewUser(
                            auth = auth,
                            nav = nav,
                            email = email,
                            password = password
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 30.dp)
                ) {
                    Text(text = "Sign Up")
                }
                TextButton(
                    onClick = {

                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp),

                    ) {
                    Text("I have an account")
                }
            }

        }
    }

}
