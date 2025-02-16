package com.example.snapcart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BestSellerScreen(navController: NavController, bestSeller: List<SubCategory>) {
    var selected by remember { mutableStateOf(bestSeller[0].name) }
    var items by remember { mutableStateOf(bestSeller[0].items) }
    var cart by remember { mutableStateOf<List<Carted>>(emptyList()) }
    LaunchedEffect(Unit){
        cart= fetchCartFromFirebase()
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        // Top Bar or Heading
        Text(
            text = selected,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color(0xFF444444)
        )

        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar for Categories
            LazyColumn(
                modifier = Modifier
                    .width(95.dp)
                    .background((Color.White))
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bestSeller) { subcategory ->
                    CategoryButton(
                        isSelected = subcategory.name == selected,
                        name = subcategory.name,
                        imageUrl = subcategory.items[0].image,
                        onClick = {
                            selected = subcategory.name
                            items = subcategory.items
                        }
                    )
                }
                items(bestSeller) { subcategory ->
                    CategoryButton(
                        isSelected = subcategory.name == selected,
                        name = subcategory.name,
                        imageUrl = subcategory.items[0].image,
                        onClick = {
                            selected = subcategory.name
                            items = subcategory.items
                        }
                    )
                }
            }
            Box(modifier = Modifier.fillMaxSize()){
                LazyColumn(){
                    bestSeller.forEach(){
                        if(selected==it.name){
                            items=it.items
                        }
                    }
                    item{
                        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                            ItemCardlook(item = items[0],cart,onCartUpdate = { updatedCart ->
                                cart = updatedCart
                            })
                            ItemCardlook(item = items[1],cart,onCartUpdate = { updatedCart ->
                                cart = updatedCart
                            })

                        }
                    }

                    item{
                        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                            ItemCardlook(item = items[2],cart,onCartUpdate = { updatedCart ->
                                cart = updatedCart
                            })
                            ItemCardlook(item = items[3],cart,onCartUpdate = { updatedCart ->
                                cart = updatedCart
                            })

                        }
                    }

                    item{
                        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                            ItemCardlook(item = items[4],cart,onCartUpdate = { updatedCart ->
                                cart = updatedCart
                            })
                            ItemCardlook(item = items[5],cart,onCartUpdate = { updatedCart ->
                                cart = updatedCart
                            })

                        }
                    }

                }
                if(!cart.isEmpty()){
                    Box (modifier= Modifier
                        .wrapContentSize()
                        .align(Alignment.BottomCenter)
                        .offset(0.dp, 0.dp)){

                        Button(onClick = { navController.navigate("checkout") },modifier= Modifier
                            .align(Alignment.BottomCenter)
                            .padding(8.dp)
                            .width(150.dp), colors = ButtonDefaults.buttonColors(Color(50,134,22,255)), shape = RoundedCornerShape(32.dp)) {
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




        }
    }
}

@Composable
fun CategoryButton(
    isSelected: Boolean,
    name: String,
    imageUrl: String,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClick() })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(35.dp))
                .background(
                    if (isSelected) Color(0xFFA7FFEB)
                    else Color(0xFFECECEC)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = null,
                modifier = if(isSelected) Modifier.size(55.dp) else Modifier.size(45.dp))

        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            Text(
                text = name,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Light,
                fontSize = 11.sp, textAlign = TextAlign.Center

                )
        }
    }
}


@Composable
fun ItemCardlook(item: Items,cart: List<Carted>,onCartUpdate:(List<Carted>)->Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .width(150.dp)
            .height(400.dp), // Fixed height for consistent card sizes
        elevation = CardDefaults.cardElevation(4.dp), // Slight elevation for a more professional look
        shape = RoundedCornerShape(12.dp), // More rounded corners for a sleek look
        colors = CardDefaults.cardColors(Color.White) // White background color for the card
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween, // Space between items
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp) // Padding for inner elements
        ) {
            // Item image with background
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Make the image background fill the width of the card
                    .height(140.dp) // Set the background height to be larger than the image
                    .background(Color(248, 247, 252, 255)) // Background color
                    .clip(RoundedCornerShape(8.dp)) // Rounded corners for background
                    .padding(10.dp) // Add padding inside the Box to center the image
            ) {
                Image(
                    painter = rememberImagePainter(item.image), // Replace with Coil or Glide for real images
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize() // Ensure image fills the available space
                        .clip(RoundedCornerShape(8.dp)) // Rounded corners for image
                )
            }

            // Spacer between image and text
            Spacer(modifier = Modifier.height(8.dp))

            // Weight and Flavour Text
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Make row take the full width
                    .padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.weight,
                    fontSize = 10.sp,
                    color = Color(37, 50, 164, 255),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(249, 250, 252))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Text(
                    text = item.flavour,
                    fontSize = 10.sp,
                    color = Color(37, 50, 164, 255),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(249, 250, 252))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            // Description Text (with fixed height for consistency)
            Text(
                text = item.desc,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2, // Fixed max lines for description
                overflow = TextOverflow.Ellipsis, // Truncate long descriptions
                modifier = Modifier.padding(vertical = 4.dp),
                fontSize = 14.sp // Direct fontSize instead of body2
            )

            // Item price
            Text(
                text = "₹${item.price}",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp),
                fontSize = 14.sp // Direct fontSize instead of body1
            )

            // Rating display
            var rating = item.rating.toFloatOrNull() ?: 0.0f
            if(rating!=0.0f) StarRatingDisplay(rating)

            AddButtonNew(item = item, cart = cart, onCartUpdate = onCartUpdate)
        }
    }
}
@Composable
fun AddButtonNew(item: Items, cart: List<Carted>,onCartUpdate:(List<Carted>)->Unit) {
    val matchingItem = cart.find { it.item.desc == item.desc }

    if (matchingItem == null || matchingItem.quantity==0) {
        SimpleAddButtonNew(item = item, cart = cart, onCartUpdate = onCartUpdate)
    } else {
        CustomAddButtonNew(item = matchingItem.item, cart = cart, onCartUpdate = onCartUpdate,matchingItem)
    }

}
@Composable
fun SimpleAddButtonNew(item: Items, cart: List<Carted>,onCartUpdate:(List<Carted>)->Unit) {
    Button(
        onClick = {
            val newcarted=Carted(item,1)
            addToCartInFirebase(newcarted)
            // Create a new list with the added item
            val updatedCart = cart + Carted(item, 1)
            onCartUpdate(updatedCart)

        },
        modifier = Modifier
            .width(110.dp)
            .height(80.dp)
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(70, 167, 31, 255))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ADD",
                color = Color(70, 167, 31, 255),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold, fontSize = 12.sp
            )
        }
    }
}
@Composable
fun CustomAddButtonNew(
    item: Items,
    cart: List<Carted>,
    onCartUpdate: (List<Carted>) -> Unit,
    carted: Carted
) {
    var qnty by remember(carted) { mutableStateOf(carted.quantity) }

    Row(
        modifier = Modifier
            .width(120.dp)
            .height(50.dp)
            .padding(10.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(70, 167, 31, 255)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            if (qnty > 0) {
            updateQuantity(
                cart,
                carted,
                qnty - 1,
                onCartUpdate = { updatedCart ->
                    qnty = qnty - 1
                    onCartUpdate(updatedCart)
                }
            )
        }
        }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_remove_24),
                contentDescription = null,
                tint = Color.White, modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = qnty.toString(),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        IconButton(onClick = {
            updateQuantity(
                cart,
                carted,
                qnty + 1,
                onCartUpdate = { updatedCart ->
                    qnty = qnty + 1
                    onCartUpdate(updatedCart)
                }
            )
        }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_add_24),
                contentDescription = null,
                tint = Color.White, modifier = Modifier.size(20.dp)
            )
        }
    }
}
private fun updateQuantity(
    cart: List<Carted>,
    carted: Carted,
    newQuantity: Int,
    onCartUpdate: (List<Carted>) -> Unit
) {
    val updatedCart = cart.map {
        if (it.item.desc == carted.item.desc) it.copy(quantity = newQuantity) else it
    }.filter { it.quantity > 0 }

    updateCartedInFirebase(carted.copy(quantity = newQuantity), onSuccess = {
        onCartUpdate(updatedCart)
    }, onFailure = { e ->
        println("Error updating Firebase: ${e.message}")
    })
}




