package com.example.snapcart

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
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
fun EmailSignUp(navController: NavController) {
    var Email by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var confmpassword by remember { mutableStateOf("") }
    val viewModel: AuthViewModel = viewModel()
    var visible by remember { mutableStateOf(false) }
    var visible2 by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity
    BackHandler {
        Toast.makeText(context, "You can't go back at this stage", Toast.LENGTH_SHORT).show()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), // Reduced horizontal padding for efficient space usage
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top image
        Image(
            painter = painterResource(id = R.drawable.image_prod),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp), // Adjusted height for efficient usage
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(100.dp) // Reduced logo size slightly
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tagline
        Text(
            "Snap It Fast, Freshness That Lasts!",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // "Sign Up" text
        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            Text(
                "Sign Up",
                fontWeight = FontWeight.Medium,


                )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Email input
        OutlinedTextField(
            value = Email,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF4C8B82),
                unfocusedIndicatorColor = Color.LightGray,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent

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

        Spacer(modifier = Modifier.height(8.dp))

        // Password input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
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

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm password input
        OutlinedTextField(
            value = confmpassword,
            onValueChange = { confmpassword = it
                if(Email.isNotEmpty() && password.length>=8 && confmpassword.length>=8){
                    isValid=true
                }},

            label = { Text(text = "Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { visible2 = !visible2 }) {
                    val image = if (visible2) {
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
            visualTransformation = if (visible2) VisualTransformation.None else PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // "Already a member" text with Sign In button
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("Already a Member?")
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = { navController.navigate(Emailsignin.name) },modifier=Modifier.offset(-8.dp,-13.dp)) {
                Text(text = "Sign In", fontWeight = FontWeight.Medium,color=Color(0xFF4C8B82))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign up button
        Button(
            onClick = {
                if (confmpassword != password) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                } else {
                    if (activity != null) {
                        viewModel.signUpWithEmail(Email, password, activity,navController)
                    } else {
                        Toast.makeText(context, "Activity is not available.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isValid) {
                    Color(0xFF4C8B82) // Active color
                } else {
                    Color.LightGray // Disabled color
                }
            ),
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp) // Adjusted height for space efficiency
        ) {
            Text(
                text = "Continue",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}







