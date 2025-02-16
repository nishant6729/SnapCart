package com.example.snapcart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersPage(navController: NavController) {
    var list by remember {
        mutableStateOf<List<Orders>>(emptyList())
    }
    var delivered by remember {
        mutableStateOf<List<Carted>>(emptyList())
    }
    var firestoreHelper=FirestoreHelper()
    var context= LocalContext.current
    val scrollState = rememberLazyListState()
    var selected by remember { mutableStateOf("orderagain") }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LaunchedEffect(Unit) {
        getOrdersFromFirebase { fetchedOrders ->
            list = fetchedOrders
        }
        delivered = getDeliveredFromFirebase()
    }
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
                        ) { navController.navigate("homescreen")
                            selected = "home" }

                        BottomBarButton(
                            text = "Orders",
                            isSelected = selected == "orderagain",
                            iconId = if (selected == "orderagain") R.drawable.orderagainselected else R.drawable.orderagain
                        ) { navController.navigate("orderspage")
                            selected = "orderagain" }

                        BottomBarButton(
                            text = "Categories",
                            isSelected = selected == "categories",
                            iconId = if (selected == "categories") R.drawable.categoriesselected else R.drawable.categories
                        ) { selected = "categories" }
                    }
                }
            }

        }
    ) { paddingValues ->
        if(list.isEmpty()){
            // Show a friendly "No orders" screen.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(245, 246, 251, 255))
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Optional: Display an image when there are no orders.
                    Image(
                        painter = painterResource(id = R.drawable.norder), // Use your own drawable resource.
                        contentDescription = "No Orders",
                        modifier = Modifier.size(150.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No current orders in process",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your orders will appear here once placed.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
        else{
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(245, 246, 251, 255))
            ) {
                items(list) { order ->
                    OrderCard(delivered,order, onorderUpdate = { updatedList ->
                        list = updatedList
                    })
                }
            }
        }
        }

}

@Composable
fun OrderCard(delivered:List<Carted>,orders: Orders, onorderUpdate: (List<Orders>) -> Unit) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.wrapContentSize()) {

            Column {
                orders.carted.forEach { carted ->
                    ItemCardHorizontal(delivered,orders, carted = carted, onCartUpdate = onorderUpdate)
                }
            }
        }
    }
}

fun getOrdersFromFirebase(onOrdersFetched: (List<Orders>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var user=FirebaseAuth.getInstance().currentUser?.uid?:return
    val productRef = db.collection("data").document(user)
    productRef.get()
        .addOnSuccessListener { document ->
            val ordersList = mutableListOf<Orders>()
            if (document != null && document.exists()) {
                val productAdminList = document.get("Orders") as? List<Map<String, Any>>
                ordersList.addAll(
                    productAdminList?.mapNotNull { orderMap ->
                        try {
                            val name = orderMap["name"] as? String ?: ""
                            val phoneno = orderMap["phoneno"] as? String ?: ""
                            val address = orderMap["address"] as? String ?: ""
                            val cartedList = (orderMap["carted"] as? List<Map<String, Any>>)?.mapNotNull { cartedMap ->
                                val itemMap = cartedMap["item"] as? Map<String, Any>
                                val item = itemMap?.let {
                                    Items(
                                        desc = it["desc"] as? String ?: "",
                                        price = it["price"] as? String ?: "",
                                        image = it["image"] as? String ?: "",

                                    )
                                }
                                val quantity = (cartedMap["quantity"] as? Number)?.toInt() ?: 0
                                item?.let { Carted(it, quantity) }
                            } ?: emptyList()
                            Orders(name, phoneno, address, cartedList)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()
                )
            }
            onOrdersFetched(ordersList)
        }
        .addOnFailureListener { exception ->
            println("Error fetching orders: $exception")
            onOrdersFetched(emptyList())
        }
}

@Composable
fun ItemCardHorizontal(delivered: List<Carted>, orders: Orders, carted: Carted, onCartUpdate: (List<Orders>) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(233, 233, 243, 255))
                .size(80.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = carted.item.image),
                contentDescription = null,
                alignment = Alignment.Center,
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center)
            )
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(120.dp)
        ) {
            Text(text = carted.item.desc, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = carted.item.weight, fontWeight = FontWeight.Light)
            Spacer(modifier = Modifier.height(0.dp))
            Text(text = "Quantity= " + carted.quantity, fontWeight = FontWeight.Normal)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.fillMaxHeight()) {
            DoneButton(delivered,orders, carted, onCartUpdate)
            Text(
                text = "₹ " + carted.item.price,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Right,
                modifier = Modifier.offset(50.dp)
            )
        }
    }
}

@Composable
fun DoneButton(delivered: List<Carted>, order: Orders, carted: Carted, onCartUpdate: (List<Orders>) -> Unit) {

    if(delivered.contains(carted)){
        Row(
            modifier = Modifier
                .width(120.dp)
                .height(80.dp)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))

                .background(Color.White)
                ,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Delivered",
                color = Color(11, 213, 68, 255),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }
    }
    else{
        Row(
            modifier = Modifier
                .width(120.dp)
                .height(80.dp)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    // Call the transaction function on button click.
                    markCartedItemAsRemoved(order, carted, onCartUpdate)
                    markCartedItemAsRemovedSecond(order, carted, onCartUpdate)
                }
                .background(Color.White)
                .border(1.dp, Color(206, 21, 21, 255), RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cancel",
                color = Color(206, 21, 21, 255),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }
    }

}

// This is the updated transaction function with additional type conversion and logging.
fun markCartedItemAsRemoved(
    order: Orders,
    carted: Carted,
    onUpdateComplete: (List<Orders>) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var user=FirebaseAuth.getInstance().currentUser?.uid?:return
    val productRef = db.collection("data").document(user)

    db.runTransaction { transaction ->
        // Get the document snapshot.
        val snapshot = transaction.get(productRef)
        println("Snapshot data: \${snapshot.data}")

        // Retrieve the Products list.
        val products = (snapshot.get("Orders") as? List<Map<String, Any>>)
            ?.toMutableList() ?: mutableListOf()

        val updatedProducts = mutableListOf<Map<String, Any>>()
        var foundMatchingOrder = false

        // Iterate over each order in Products.
        for (orderMap in products) {
            if (orderMap["name"] == order.name && orderMap["phoneno"] == order.phoneno) {
                foundMatchingOrder = true
                val cartedList = (orderMap["carted"] as? List<Map<String, Any>>)
                    ?.toMutableList() ?: mutableListOf()
                val iterator = cartedList.iterator()
                var removed = false

                // Iterate over carted items.
                while (iterator.hasNext()) {
                    val cartedMap = iterator.next()
                    val itemMap = cartedMap["item"] as? Map<String, Any>
                    val storedQuantity = (cartedMap["quantity"] as? Number)?.toInt() ?: 0

                    if (itemMap?.get("desc") == carted.item.desc && storedQuantity == carted.quantity) {
                        println("Found matching carted item. Removing it.")
                        iterator.remove()
                        removed = true
                        break // Remove one instance only.
                    }
                }

                // Only add the order back if it still has items.
                if (cartedList.isNotEmpty()) {
                    val updatedOrder = orderMap.toMutableMap().apply { put("carted", cartedList) }
                    updatedProducts.add(updatedOrder)
                } else {
                    println("Order for \${order.name} removed because no carted items remain.")
                }
            } else {
                updatedProducts.add(orderMap)
            }
        }

        if (!foundMatchingOrder) {
            println("Order matching \${order.name} not found in Products.")
        }

        println("Updated Products: \$updatedProducts")

        // Update only the Products field.
        transaction.update(productRef, "Orders", updatedProducts)
    }
        .addOnSuccessListener {
            println("Transaction successful: Carted item removed from Products.")
            // Refresh the list.
            getOrdersFromFirebase(onUpdateComplete)
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
            println("Transaction failure: \${e.message}")
        }
}
fun markCartedItemAsRemovedSecond(
    order: Orders,
    carted: Carted,
    onUpdateComplete: (List<Orders>) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var user=FirebaseAuth.getInstance().currentUser?.uid?:return
    val productRef = db.collection("data").document("bLh8HvjILMdoj6JlVBDg")

    db.runTransaction { transaction ->
        // Get the document snapshot.
        val snapshot = transaction.get(productRef)
        println("Snapshot data: \${snapshot.data}")

        // Retrieve the Products list.
        val products = (snapshot.get("Products") as? List<Map<String, Any>>)
            ?.toMutableList() ?: mutableListOf()

        val updatedProducts = mutableListOf<Map<String, Any>>()
        var foundMatchingOrder = false

        // Iterate over each order in Products.
        for (orderMap in products) {
            if (orderMap["name"] == order.name && orderMap["phoneno"] == order.phoneno) {
                foundMatchingOrder = true
                val cartedList = (orderMap["carted"] as? List<Map<String, Any>>)
                    ?.toMutableList() ?: mutableListOf()
                val iterator = cartedList.iterator()
                var removed = false

                // Iterate over carted items.
                while (iterator.hasNext()) {
                    val cartedMap = iterator.next()
                    val itemMap = cartedMap["item"] as? Map<String, Any>
                    val storedQuantity = (cartedMap["quantity"] as? Number)?.toInt() ?: 0

                    if (itemMap?.get("desc") == carted.item.desc && storedQuantity == carted.quantity) {
                        println("Found matching carted item. Removing it.")
                        iterator.remove()
                        removed = true
                        break // Remove one instance only.
                    }
                }

                // Only add the order back if it still has items.
                if (cartedList.isNotEmpty()) {
                    val updatedOrder = orderMap.toMutableMap().apply { put("carted", cartedList) }
                    updatedProducts.add(updatedOrder)
                } else {
                    println("Order for \${order.name} removed because no carted items remain.")
                }
            } else {
                updatedProducts.add(orderMap)
            }
        }

        if (!foundMatchingOrder) {
            println("Order matching \${order.name} not found in Products.")
        }

        println("Updated Products: \$updatedProducts")

        // Update only the Products field.
        transaction.update(productRef, "Products", updatedProducts)
    }
        .addOnSuccessListener {
            println("Transaction successful: Carted item removed from Products.")
            // Refresh the list.
            getOrdersFromFirebase(onUpdateComplete)
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
            println("Transaction failure: \${e.message}")
        }
}
suspend fun getDeliveredFromFirebase(): List<Carted> {
    val db = FirebaseFirestore.getInstance()
    val productRef = db.collection("data").document("bLh8HvjILMdoj6JlVBDg")

    return try {
        val documentSnapshot = productRef.get().await()
        if (documentSnapshot.exists()) {
            // Get the Delivered field, which is expected to be a list of maps.
            val deliveredList = documentSnapshot.get("Delivered") as? List<Map<String, Any>>
            deliveredList?.mapNotNull { deliveredMap ->
                // Extract the item map.
                val itemMap = deliveredMap["item"] as? Map<String, Any>
                val item = itemMap?.let {
                    Items(
                        desc = it["desc"] as? String ?: "",
                        price = it["price"] as? String ?: "",
                        image = it["image"] as? String ?: "",
                        weight = it["weight"] as? String ?: "",
                        flavour = it["flavour"] as? String ?: "",
                        offers = it["offers"] as? String ?: "",
                        rating = it["rating"] as? String ?: ""
                    )
                }
                // Extract the quantity value (converted to an Int).
                val quantity = (deliveredMap["quantity"] as? Number)?.toInt() ?: 0
                item?.let { Carted(it, quantity) }
            } ?: emptyList()
        } else {
            emptyList()
        }
    } catch (e: Exception) {
        println("Error fetching delivered items: ${e.message}")
        emptyList()
    }
}
