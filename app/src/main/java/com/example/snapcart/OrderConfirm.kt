package com.example.snapcart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun OrderConfirm(navController: NavController){
    LaunchedEffect(Unit) {
        // Delay for 5 seconds
        delay(3000)
        // Navigate to the home route
        navController.navigate("homescreen") {
            // Optionally, remove the OrderConfirm screen from the back stack
            popUpTo("orderConfirm") { inclusive = true }
        }
    }
    Column(modifier= Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier= Modifier
            .fillMaxWidth()
            .align(
                Alignment.CenterHorizontally
            ), horizontalArrangement = Arrangement.Center){
            Image(painter = painterResource(id = R.drawable.orderplaced), contentDescription = null,modifier=Modifier.size(200.dp))
        }
        Spacer(modifier=Modifier.height(80.dp))
        Row(modifier= Modifier
            .fillMaxWidth()
            .align(
                Alignment.CenterHorizontally
            ), horizontalArrangement = Arrangement.Center){
            Text(text = "Order Placed", fontWeight = FontWeight.Bold, fontSize = 28.sp)
        }
    }
}