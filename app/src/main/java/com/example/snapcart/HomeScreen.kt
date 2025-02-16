package com.example.snapcart


import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(navController: NavController) {
   ScrollableScreen(navController)


}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollableScreen(navController: NavController) {
    var search by remember {
        mutableStateOf("")
    }
    val FirestoreHelper=FirestoreHelper()
    var selected by remember { mutableStateOf("home") }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberLazyListState()
    var Subcategories by remember { mutableStateOf<List<SubCategory>>(emptyList()) }
    var bestSeller by remember{ mutableStateOf<SubCategories>(SubCategories()) }
    var listofSubcategory by remember{ mutableStateOf<List<SubCategory>>(emptyList()) }
    var cart by remember { mutableStateOf<List<Carted>>(emptyList()) }
    val firestoreHelper=FirestoreHelper()
    var context= LocalContext.current
    LaunchedEffect(Unit) {
        Subcategories = fetchSubCategories()
//        val fetchedBestSeller = fetchSubCategoriesFromFirestore()
//        if (fetchedBestSeller.subcategory.isNotEmpty()) {
//            bestSeller = fetchedBestSeller
//        } else {
//            Log.e("FirestoreDebug", "Fetched bestSeller is empty!")
//        }
        listofSubcategory=fetchSubCategoriesFromFirestore()
        cart= fetchCartFromFirebase()


    }
    Box(modifier=Modifier.fillMaxSize()){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Snapcart in",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "16 minutes",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                fontSize = 22.sp,
                                color = Color.Black
                            )
                        }
                    },
                    actions = {
                        Row(modifier=Modifier.wrapContentWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                            Icon(painter = painterResource(id = R.drawable.logout), contentDescription = null,modifier=Modifier.size(24.dp))

                            TextButton(onClick = {
                                firestoreHelper.logoutUser(context) { success ->
                                    if (success) {
                                        navController.navigate(StartRoute.name) {
                                            popUpTo(0) // Clear the back stack to avoid returning to Home
                                        }
                                    }
                                }
                            }) {
                                Text(text = "Logout")
                            }
                        }

                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    modifier = Modifier.background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(236, 189, 49, 255), // Gradient start color

                                Color(246, 210, 100, 255),
                                Color(236, 188, 44, 255)// Gradient end color
                            )
                        )
                    ),
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = scrollState.firstVisibleItemIndex == 0,
                    enter = slideInVertically(
                        initialOffsetY = { it } // Slide in from the bottom
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it } // Slide out to the bottom
                    )
                ) {
                    BottomAppBar(
                        modifier = Modifier.background(Color.White),
                        containerColor = Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            BottomBarButton(
                                text = "Home",
                                isSelected = selected == "home",
                                iconId = if (selected == "home") R.drawable.homeselected else R.drawable.home
                            ) {navController.navigate("homescreen")
                                selected = "home" }

                            BottomBarButton(
                                text = "Orders",
                                isSelected = selected == "orderagain",
                                iconId = if (selected == "orderagain") R.drawable.orderagainselected else R.drawable.orderagain
                            ) {navController.navigate("orderspage")
                                selected = "orderagain" }

                            BottomBarButton(
                                text = "Categories",
                                isSelected = selected == "categories",
                                iconId = if (selected == "categories") R.drawable.categoriesselected else R.drawable.categories
                            ) {navController.navigate("categoriespage")
                                selected = "categories" }
                        }
                    }
                }

            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(236, 189, 49, 255), // Gradient start color

                                    Color(246, 210, 100, 255),
                                    Color(236, 188, 44, 255) // Gradient end color
                                )
                            )
                        )
                        .padding(innerPadding)
                ) {
                    var selectedcategory by remember { mutableStateOf("All") }
                    // Fixed Search Box
                    AnimatedPlaceholderTextField(innerPadding,navController)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp), // Add padding for the LazyRow
                        horizontalArrangement = Arrangement.spacedBy(16.dp), // Consistent spacing between items
                        contentPadding = PaddingValues(horizontal = 16.dp) // Padding around the start and end
                    ) {
                        items(
                            listOf(
                                "All",
                                "Electrical",
                                "Gifts",
                                "Baby",
                                "Beauty",
                                "Premium"
                            )
                        ) { category ->
                            val isSelected = selectedcategory == category
                            CategoryRow(
                                title = category,
                                icon = when (category) {
                                    "All" -> R.drawable.groceries
                                    "Electrical" -> R.drawable.electronics
                                    "Gifts" -> R.drawable.gift
                                    "Baby" -> R.drawable.baby
                                    "Beauty" -> R.drawable.beauty
                                    "Premium" -> R.drawable.premium
                                    else -> R.drawable.all // Default fallback
                                },
                                isSelected = isSelected,
                                onClick = {
                                    selectedcategory = category
                                } // Update the selected category
                            )
                        }
                    }


                    // LazyColumn starts below the search box
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .background(Color.White)
                    ) {
                        item {
                            Image(
                                painter = painterResource(id = R.drawable.banner),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            Column {
                                Text(
                                    text = "Recommended",
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(start = 22.dp, 16.dp),
                                    fontSize = 20.sp
                                )
//                            Row {
//                                Spacer(modifier=Modifier.width(8.dp))
////                                BestSellerCard(R.drawable.chipsandnamkeen,bestSeller,navController)
//                                Lazy
//                            }
//                            Row{
//
//                            }
                                Spacer(modifier = Modifier.height(16.dp))
                                Card(
                                    modifier = Modifier
                                        .padding(start = 20.dp, 8.dp)
                                        .width(200.dp)
                                        .height(220.dp)
                                        .clickable {
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                "subCategories",
                                                Subcategories
                                            )
                                            navController.navigate("bestseller_Screen")
                                        }, // Fixed height for consistent card sizes
                                    elevation = CardDefaults.cardElevation(8.dp), // Slight elevation for a more professional look
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
                                                .background(
                                                    Color(
                                                        248,
                                                        247,
                                                        252,
                                                        255
                                                    )
                                                ) // Background color
                                                .clip(RoundedCornerShape(8.dp)) // Rounded corners for background
                                                .padding(2.dp) // Add padding inside the Box to center the image
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.bestsellers), // Replace with Coil or Glide for real images
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxSize() // Ensure image fills the available space
                                                // Rounded corners for image
                                            )
                                        }

                                        // Spacer between image and text


                                        // Weight and Flavour Text
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth() // Make row take the full width
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Bestsellers",
                                                fontSize = 20.sp,
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                            )

                                        }
                                    }
                                }    // Description Text (with fixed height for consistency)
                            }

                        }





                        items(Subcategories) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = it.name,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(16.dp)
                            )
                            var itemsList = it.items

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ItemCard(
                                        item = itemsList[0],
                                        cart = cart,
                                        onCartUpdate = { updatedCart ->
                                            cart = updatedCart
                                        })
                                    ItemCard(
                                        item = itemsList[1],
                                        cart = cart,
                                        onCartUpdate = { updatedCart ->
                                            cart = updatedCart
                                        })

                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ItemCard(
                                        item = itemsList[2],
                                        cart = cart,
                                        onCartUpdate = { updatedCart ->
                                            cart = updatedCart
                                        })
                                    ItemCard(
                                        item = itemsList[3],
                                        cart = cart,
                                        onCartUpdate = { updatedCart ->
                                            cart = updatedCart
                                        })

                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ItemCard(
                                        item = itemsList[4],
                                        cart = cart,
                                        onCartUpdate = { updatedCart ->
                                            cart = updatedCart
                                        })
                                    ItemCard(
                                        item = itemsList[5],
                                        cart = cart,
                                        onCartUpdate = { updatedCart ->
                                                cart = updatedCart
                                        })

                                }

                            }
                        }

                    }
                }
            }

        )
        if(!cart.isEmpty()){
            Box (modifier= Modifier
                .wrapContentSize()
                .align(Alignment.BottomCenter)
                .offset(0.dp, -80.dp)){

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


@Composable
fun BottomBarButton(
    text: String,
    isSelected: Boolean,
    iconId: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier.offset(0.dp,-6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .offset(0.dp, -6.dp)
            )

            Text(
                text = text,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp,
                color = if (isSelected) Color.Black else Color.Gray, modifier = Modifier.offset(0.dp,-5.dp)
            )
        }
    }

}
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AnimatedPlaceholderTextField(innerPadding: PaddingValues, navController: NavController) {
    var search by remember { mutableStateOf("") }
    val placeholderTexts = listOf("Search items...", "Search 'Fruits'", "Search 'Vegetables'")
    var currentPlaceholderIndex by remember { mutableStateOf(0) }

    // Update the placeholder text every 2 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            currentPlaceholderIndex = (currentPlaceholderIndex + 1) % placeholderTexts.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // The TextField is set to readOnly to disable input.
        TextField(
            value = search,
            onValueChange = { search = it },
            readOnly = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    contentDescription = "Search icon"
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_mic_24),
                    contentDescription = "Mic icon",
                    modifier = Modifier.clickable {
                        // Trigger Mic Action here, if needed
                    }
                )
            },
            placeholder = {
                AnimatedContent(
                    targetState = placeholderTexts[currentPlaceholderIndex],
                    transitionSpec = {
                        slideInVertically { it } + fadeIn() with
                                slideOutVertically { -it } + fadeOut()
                    }
                ) { placeholderText ->
                    Text(text = placeholderText)
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Overlay an invisible clickable layer on top of the TextField.
        Box(
            modifier = Modifier
                .matchParentSize() // Fill the entire size of the parent Box
                .clickable {
                    navController.navigate("searchscreen")
                }
        )
    }
}



@Composable
fun CategoryRow(title: String, icon: Int, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable(
                onClick = onClick,
                indication = null, // Removes the ripple effect
                interactionSource = remember { MutableInteractionSource() } // Prevents any interaction
            ) // Ensure the onClick is triggered
            .height(60.dp)
            .width(75.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isSelected) Color.Black else Color(90, 89, 87, 255)
        )
        Text(
            text = title,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color =Color.Black,
            modifier = Modifier.padding(top = 4.dp)
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .width(60.dp)
                    .background(Color.Black)
            )
        }
    }
}
suspend fun fetchSubCategories(): List<SubCategory> {
    return withContext(Dispatchers.IO) {
        val firestore = FirebaseFirestore.getInstance()
        val collectionName = "data"
        val documentId = "1ZfgFwOhuk5Vvc6izgnG"
        try {
            val documentSnapshot = firestore.collection(collectionName).document(documentId).get().await()
            val categories = documentSnapshot.get("Categories") as? List<Map<String, Any>>
            categories?.drop(1)?.map { map ->
                val name = map["name"] as? String ?: ""
                val itemsList = (map["items"] as? List<Map<String, Any>>)?.map { itemMap ->
                    Items(
                        weight = itemMap["weight"] as? String ?: "",
                        flavour = itemMap["flavour"] as? String ?: "",
                        desc = itemMap["desc"] as? String ?: "",
                        image = itemMap["image"] as? String ?: "",
                        offers = itemMap["offer"] as? String ?: "",
                        price = itemMap["price"] as? String ?: "",
                        rating = itemMap["rating"] as? String ?: ""
                    )
                } ?: emptyList()
                SubCategory(name = name, items = itemsList)
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
@Composable
fun ItemCard(item: Items,cart: List<Carted>,onCartUpdate:(List<Carted>)->Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(180.dp)
            .height(400.dp), // Fixed height for consistent card sizes
        elevation = CardDefaults.cardElevation(8.dp), // Slight elevation for a more professional look
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
                modifier = Modifier.padding(vertical = 2.dp),
                fontSize = 14.sp // Direct fontSize instead of body2
            )
            Spacer(modifier =Modifier.height(8.dp))
            // Item price
            Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Text(
                    text = "₹${item.price}",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 2.dp),
                    fontSize = 14.sp // Direct fontSize instead of body1
                )
                Text(
                    text = item.offers,
                    fontSize = 10.sp,
                    color = Color(37, 50, 164, 255),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(249, 250, 252))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
            Spacer(modifier =Modifier.height(8.dp))
            // Rating display
            var rating = item.rating.toFloatOrNull() ?: 0.0f
            if(rating!=0.0f) StarRatingDisplay(rating)
            

            //Add Button
            AddButton(item, cart, onCartUpdate)
        }
    }
}
@Composable
fun AddButton(item: Items, cart: List<Carted>,onCartUpdate:(List<Carted>)->Unit) {
    val matchingItem = cart.find { it.item.desc == item.desc }

    if (matchingItem == null || matchingItem.quantity==0) {
        SimpleAddButton(item = item, cart = cart, onCartUpdate = onCartUpdate)
    } else {
        CustomAddButton(item = matchingItem.item, cart = cart, onCartUpdate = onCartUpdate,matchingItem)
    }

}

@Composable
fun SimpleAddButton(item: Items, cart: List<Carted>,onCartUpdate:(List<Carted>)->Unit) {
    Button(
        onClick = {
            val newcarted=Carted(item,1)
            addToCartInFirebase(newcarted)
            // Create a new list with the added item
            val updatedCart = cart + Carted(item, 1)
            onCartUpdate(updatedCart)

        },
        modifier = Modifier
            .fillMaxWidth()
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
                fontWeight = FontWeight.Bold, fontSize = 16.sp
            )
        }
    }
}
@Composable
fun CustomAddButton(
    item: Items,
    cart: List<Carted>,
    onCartUpdate: (List<Carted>) -> Unit,
    carted: Carted
) {
    var qnty by remember(carted) { mutableStateOf(carted.quantity) }

    Row(
        modifier = Modifier
            .width(150.dp)
            .height(65.dp)
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
                tint = Color.White
            )
        }

        Text(
            text = qnty.toString(),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
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
                tint = Color.White
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


fun updateCartedInFirebase(
    carted: Carted,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: return // Ensure user is authenticated

    val userCartRef = db.collection("data").document(userId) // Use UID as document name

    userCartRef.get().addOnSuccessListener { document ->
        try {
            val cartList = (document.get("Cart") as? List<Map<String, Any>>)?.toMutableList()
                ?: mutableListOf()

            // Create a new list by updating or removing the matching item
            val updatedCartList = cartList.mapNotNull { cartItem ->
                val itemMap = cartItem["item"] as? Map<String, Any> ?: return@mapNotNull cartItem

                if (itemMap["desc"] == carted.item.desc) {
                    if (carted.quantity > 0) {
                        // Update the item's quantity if greater than 0
                        mapOf(
                            "item" to itemMap,
                            "quantity" to carted.quantity
                        )
                    } else {
                        // Return null to remove the item if quantity is 0
                        null
                    }
                } else {
                    // Keep unaffected items as-is
                    cartItem
                }
            }

            // Write updated list back to Firebase
            userCartRef.update("Cart", updatedCartList)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }

        } catch (e: Exception) {
            onFailure(e)
        }
    }.addOnFailureListener { onFailure(it) }
}





// Yha se change start hua hai
//fun addToCartInFirebase(carted: Carted) {
//    val db = FirebaseFirestore.getInstance()
//    val userCartRef = db.collection("data").document("1ZfgFwOhuk5Vvc6izgnG")
//
//    // Prepare the map with the full `item` object
//    val cartMap = mapOf(
//        "item" to mapOf( // Convert item fields to a nested map
//
//            "desc" to carted.item.desc,
//            "price" to carted.item.price,
//            "image" to carted.item.image
//        ),
//        "quantity" to carted.quantity
//    )
//
//    // Add the map to the "Cart" array field
//    userCartRef.update("Cart", FieldValue.arrayUnion(cartMap))
//        .addOnSuccessListener {
//            println("Item successfully added to Cart in Firestore!")
//        }
//        .addOnFailureListener { e ->
//            println("Error adding item to Cart: ${e.message}")
//        }
//}
// Yha khatam hua hai




fun addToCartInFirebase(carted: Carted) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid // Get authenticated user's UID

    if (userId != null) {
        val userCartRef = db.collection("data").document(userId) // Use UID as document name

        val cartMap = mapOf(
            "item" to mapOf(
                "desc" to carted.item.desc,
                "price" to carted.item.price,
                "image" to carted.item.image
            ),
            "quantity" to carted.quantity
        )

        userCartRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Document exists → Update the Cart list
                userCartRef.update("Cart", FieldValue.arrayUnion(cartMap))
                    .addOnSuccessListener {
                        println("Item successfully added to Cart in Firestore!")
                    }
                    .addOnFailureListener { e ->
                        println("Error adding item to Cart: ${e.message}")
                    }
            } else {
                // Document doesn't exist → Create a new one with Cart list
                val newUserData = mapOf("Cart" to listOf(cartMap))

                userCartRef.set(newUserData)
                    .addOnSuccessListener {
                        println("Cart document created, item added!")
                    }
                    .addOnFailureListener { e ->
                        println("Error creating cart document: ${e.message}")
                    }
            }
        }.addOnFailureListener { e ->
            println("Error checking document: ${e.message}")
        }
    } else {
        println("Error: User not authenticated!")
    }
}



suspend fun fetchCartFromFirebase(): List<Carted> {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid // Get authenticated user's UID

    return if (userId != null) {
        val userCartRef = db.collection("data").document(userId) // Use UID as document name

        try {
            val document = userCartRef.get().await() // Await Firestore document fetch

            if (document.exists()) {
                val cartList = document.get("Cart") as? List<Map<String, Any>> ?: emptyList()

                // Convert the retrieved Firestore Cart data to Carted objects
                cartList.map { cartItem ->
                    val itemMap = cartItem["item"] as? Map<String, Any> ?: emptyMap()

                    val item = Items(
                        desc = itemMap["desc"] as? String ?: "",
                        price = itemMap["price"] as? String ?: "",
                        image = itemMap["image"] as? String ?: ""
                    )

                    // Extract quantity safely
                    val quantity = (cartItem["quantity"] as? Long)?.toInt() ?: 0

                    Carted(item, quantity)
                }
            } else {
                println("No Cart found for user.")
                emptyList() // Return empty list if no cart exists
            }
        } catch (e: Exception) {
            println("Error fetching cart: ${e.message}")
            emptyList() // Return empty list on failure
        }
    } else {
        println("Error: User not authenticated!")
        emptyList() // Return empty list if user is not logged in
    }
}




@Composable
fun StarRatingDisplay(
    rating: Float, // Rating between 0 to 5
    maxStars: Int = 5,
    starSize: Float = 40f, // Size of each star
    starColor: Color = Color(0xFFFFA500), // Orange color for filled stars
    emptyStarColor: Color = Color.LightGray // Gray color for empty stars
) {
    Canvas(modifier = Modifier.size((maxStars * starSize).dp, starSize.dp)) {
        val spacing = 8.dp.toPx() // Space between stars
        val startX = 0f

        for (i in 0 until maxStars) {
            val starRating = rating - i
            val fillAmount = when {
                starRating >= 1 -> 1f // Fully filled
                starRating > 0 -> starRating // Partially filled
                else -> 0f // Empty
            }
            translate(left = startX + (i * (starSize + spacing))) {
                drawStar(
                    size = starSize,
                    color = starColor,
                    fillAmount = fillAmount,
                    emptyColor = emptyStarColor
                )
            }
        }
    }
}

fun DrawScope.drawStar(
    size: Float,
    color: Color,
    fillAmount: Float,
    emptyColor: Color
) {
    val outerRadius = size / 2
    val innerRadius = size / 4
    val path = Path().apply {
        createStarPath(
            centerX = outerRadius,
            centerY = outerRadius,
            outerRadius = outerRadius,
            innerRadius = innerRadius
        )
    }

    // Draw the full star with empty color
    drawPath(path, color = emptyColor)

    // Clip and draw the filled portion
    drawIntoCanvas {canvas ->
        canvas.save()
        canvas.clipRect(0f, 0f, size * fillAmount, size)
        drawPath(path, color = color)
        canvas.restore()
    }
}

fun Path.createStarPath(centerX: Float, centerY: Float, outerRadius: Float, innerRadius: Float) {
    val angleStep = Math.toRadians(36.0) // Angle step in radians for a 5-pointed star
    var angle = Math.toRadians(-90.0) // Start at the top point

    for (i in 0 until 10) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val x = centerX + (radius * cos(angle)).toFloat()
        val y = centerY + (radius * sin(angle)).toFloat()

        if (i == 0) moveTo(x, y) else lineTo(x, y)
        angle += angleStep
    }
    close()
}
//suspend fun fetchSubCategoriesFromFirestore(): SubCategories {
//    val db = FirebaseFirestore.getInstance()
//    val documentRef = db.collection("data").document("1ZfgFwOhuk5Vvc6izgnG")
//
//    return withContext(Dispatchers.IO) {
//        try {
//            val documentSnapshot = documentRef.get().await()
//            if (!documentSnapshot.exists()) {
//                Log.e("FirestoreDebug", "Document does not exist")
//                return@withContext SubCategories()
//            }
//
//            val subCatMap = documentSnapshot.get("SubCat") as? Map<String, List<Map<String, Any>>>
//                ?: run {
//                    Log.e("FirestoreDebug", "SubCat field is missing or not a Map")
//                    return@withContext SubCategories()
//                }
//
//            val subcategoryList = subCatMap.mapNotNull { (subcategoryName, subcategoryArray) ->
//                if (subcategoryArray.isNullOrEmpty()) {
//                    Log.w("FirestoreDebug", "Empty subcategory array for $subcategoryName")
//                    return@mapNotNull null
//                }
//
//                val itemsList = subcategoryArray.mapNotNull { itemData ->
//                    val itemMap = itemData as? Map<String, Any>
//                    itemMap?.let {
//                        Items(
//                            weight = it["weight"] as? String ?: "",
//                            flavour = it["flavour"] as? String ?: "",
//                            desc = it["desc"] as? String ?: "",
//                            image = it["image"] as? String ?: "",
//                            offers = it["offers"] as? String ?: "",
//                            price = it["price"] as? String ?: "",
//                            rating = it["rating"] as? String ?: ""
//                        )
//                    }
//                }
//
//                SubCategory(
//                    name = subcategoryName,
//                    items = itemsList
//                )
//            }
//
//            if (subcategoryList.isEmpty()) {
//                Log.e("FirestoreDebug", "No subcategories found")
//            }
//
//            Log.d("FirestoreDebug", "Fetched Subcategories: $subcategoryList")
//
//            SubCategories(
//                name = "DefaultName",
//                subcategory = subcategoryList
//            )
//        } catch (e: Exception) {
//            Log.e("FirestoreDebug", "Error fetching data: ${e.message}", e)
//            SubCategories()
//        }
//    }
//}
suspend fun fetchSubCategoriesFromFirestore(): List<SubCategory> {
    return withContext(Dispatchers.IO) {
        val firestore = FirebaseFirestore.getInstance()
        val collectionName = "data"
        val documentId = "1ZfgFwOhuk5Vvc6izgnG"

        try {
            // Get the document snapshot
            val documentSnapshot = firestore.collection(collectionName).document(documentId).get().await()

            // Retrieve the SubCat field as a Map<String, List<Map<String, Any>>>
            val subCatMap = documentSnapshot.get("SubCat") as? Map<String, List<Map<String, Any>>>
                ?: return@withContext emptyList() // Return empty list if SubCat is not found or in expected format

            // Process the map into SubCategory objects
            val subCategoryList = subCatMap.map { (subcategoryName, subcategoryArray) ->
                // Convert each subcategory array into a list of Items
                val itemsList = subcategoryArray.mapNotNull { itemData ->
                    val itemMap = itemData as? Map<String, Any>
                    itemMap?.let {
                        Items(
                            weight = it["weight"] as? String ?: "",
                            flavour = it["flavour"] as? String ?: "",
                            desc = it["desc"] as? String ?: "",
                            image = it["image"] as? String ?: "",
                            offers = it["offers"] as? String ?: "",
                            price = it["price"] as? String ?: "",
                            rating = it["rating"] as? String ?: ""
                        )
                    }
                }

                // Create SubCategory object for each subcategory in SubCat
                SubCategory(
                    name = subcategoryName,
                    items = itemsList
                )
            }

            // Return the list of SubCategory objects
            subCategoryList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Return empty list if any exception oc


        }
    }
}










        @Composable
fun BestSellerCard(image:Int,bestSeller:SubCategories,navController: NavController){
    Card(modifier= Modifier
        .size(150.dp)
        .clip(RoundedCornerShape(8.dp))
        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
        .clickable {
            navController.currentBackStackEntry?.savedStateHandle?.set("subCategories", bestSeller)
            navController.navigate("bestseller_Screen")
        }, colors = CardDefaults.cardColors(Color.Transparent)) {
        Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,modifier=Modifier.padding(16.dp)){
            Image(painter = painterResource(id = image) , contentDescription = null,modifier= Modifier
                .fillMaxWidth()
                .height(85.dp))
            Spacer(modifier=Modifier.height(16.dp))
            Text(text = bestSeller.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
suspend fun fetchAndStoreItems() {
    withContext(Dispatchers.IO) {
        val firestore = FirebaseFirestore.getInstance()
        val collectionName = "data"
        val documentId = "1ZfgFwOhuk5Vvc6izgnG"
        val targetDocumentId = "itemsList" // Document to store items list
        try {
            val documentSnapshot = firestore.collection(collectionName).document(documentId).get().await()
            val categories = documentSnapshot.get("Categories") as? List<Map<String, Any>>

            // Extract items list from all categories
            val allItems = categories?.flatMap { categoryMap ->
                (categoryMap["items"] as? List<Map<String, Any>>)?.map { itemMap ->
                    Items(
                        weight = itemMap["weight"] as? String ?: "",
                        flavour = itemMap["flavour"] as? String ?: "",
                        desc = itemMap["desc"] as? String ?: "",
                        image = itemMap["image"] as? String ?: "",
                        offers = itemMap["offer"] as? String ?: "",
                        price = itemMap["price"] as? String ?: "",
                        rating = itemMap["rating"] as? String ?: ""
                    )
                } ?: emptyList()
            } ?: emptyList()

            // Push the extracted items list back into Firestore
            val itemsMap = allItems.map { item ->
                mapOf(
                    "weight" to item.weight,
                    "flavour" to item.flavour,
                    "desc" to item.desc,
                    "image" to item.image,
                    "offer" to item.offers,
                    "price" to item.price,
                    "rating" to item.rating
                )
            }
            firestore.collection("items").document(targetDocumentId).set(mapOf("items" to itemsMap)).await()

            Log.d("Firestore", "Items successfully stored in Firestore")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Firestore", "Error fetching/storing items: ${e.message}")
        }
    }
}





