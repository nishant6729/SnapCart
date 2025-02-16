package com.example.snapcart

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun Emailsignin(navController: NavController) {
    var Email by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    val viewModel: AuthViewModel = viewModel()
    var visible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity

    // Handling back press explicitly
    BackHandler {
        Toast.makeText(context, "You can't go back at this stage", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Distributes content to fit screen
    ) {
        // Top Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.image_prod),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), // Reduce height to fit content
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(120.dp) // Adjust size for balance
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Snap It Fast, Freshness That Lasts!", fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(32.dp))

            Text("Login", fontWeight = FontWeight.Medium, modifier = Modifier.offset(0.dp, 24.dp))
        }

        // Middle Content
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = Email,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF4C8B82),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedLabelColor = Color(0xFF4C8B82)
                ),
                onValueChange = { Email = it },
                label = { Text(text = "Email ID") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_email_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it
                    if(password.length>=8 && Email.isNotEmpty()){
                        isValid=true
                    }},
                label = { Text(text= "Password") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { visible = !visible }) {
                        val image = if (visible) {
                            R.drawable.baseline_visibility_24
                        } else {
                            R.drawable.baseline_visibility_off_24
                        }
                        Icon(
                            painter = painterResource(id = image),
                            contentDescription = null,
                            tint = Color(0xFF4C8B82)
                        )
                    }
                },
                visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation()
            )
        }

        // Bottom Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("New Member?")
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = { navController.navigate(Emailsignup.name) }, modifier = Modifier.offset(-8.dp, -12.dp)) {
                    Text(text = "Sign Up", fontWeight = FontWeight.Medium, color = Color(0xFF4C8B82))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (activity != null) {
                        viewModel.signInWithEmail(Email, password, activity) { success ->
                            if (success) {
                                val phoneno = ""
                                navController.navigate(mainpage.name+"/$phoneno"+"/$Email")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Authentication failed. Please check your credentials.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Activity is not available.", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isValid) Color(0xFF4C8B82) else Color.LightGray
                ),
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp), // Adjusted for better appearance
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Continue",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
