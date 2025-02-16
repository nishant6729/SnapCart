package com.example.snapcart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AfterSearchScreen(navController: NavController,searched:String){
    var itemslist by remember {
        mutableStateOf<List<Items>> (emptyList())
    }
    var filteredlist by remember {
        mutableStateOf<List<Items>> (emptyList())
    }
    var cart by remember { mutableStateOf<List<Carted>>(emptyList()) }
    LaunchedEffect(Unit){
        itemslist=fetchStoredItems()
        cart=fetchCartFromFirebase()
    }
    if(searched.isNotEmpty()){
        filteredlist=itemslist
            .filter {
                it.desc.contains(searched, ignoreCase = true) }
            .sortedBy { it.desc }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp)
            .background(
                Color.White
            ) ){
            Spacer(modifier = Modifier.height(32.dp))
            Spacer(modifier = Modifier.height(40.dp))
            Column{
                Text("Showing Result for '${searched}'", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,modifier=Modifier.padding(16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(filteredlist) {
                        ItemCard(
                            item = it,
                            cart = cart,
                            onCartUpdate = { updatedCart ->
                                cart = updatedCart
                            })
                    }
                }
            }
        }

    }
    if(!cart.isEmpty()){
        Box (modifier= Modifier
            .wrapContentSize()

            .offset(110.dp, 650.dp)){

            Button(onClick = { navController.navigate("checkout") },modifier= Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .width(150.dp), colors = ButtonDefaults.buttonColors(Color(50,134,22,255)), shape = RoundedCornerShape(32.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically){
                    Column {
                        Text("View cart", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier=Modifier.height(4.dp))
                        Text(cart.size.toString()+" ITEM", fontWeight = FontWeight.Light,color=Color.White, fontSize = 13.sp)

                    }
                    Spacer(modifier=Modifier.width(20.dp))
                    Icon(painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24), contentDescription = null,tint= Color.White)

                }
            }

        }
    }

}