package com.example.snapcart

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressPage(navController: NavController){
    var name by remember {
        mutableStateOf("")
    }
    var phoneno by remember {
        mutableStateOf("")
    }
    var housename by remember {
        mutableStateOf("")
    }
    var locality by remember {
        mutableStateOf("")
    }
    var city by remember {
        mutableStateOf("")
    }
    var address by remember {
        mutableStateOf("")
    }
    var context= LocalContext.current
    var orders by remember{ mutableStateOf<List<Carted>>(emptyList()) }
    LaunchedEffect(Unit){
        orders=fetchCartFromFirebase()
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically){
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24), contentDescription =null )
                    }
                    Text(text = "Add details", fontSize = 18.sp,color= Color.DarkGray, fontWeight = FontWeight.SemiBold)
                }
                Row(modifier=Modifier.padding(16.dp)) {
                    Icon(painter = painterResource(id = R.drawable.outline_shopping_cart_24), contentDescription =null, tint = Color(50,134,22,255), modifier = Modifier.size(20.dp) )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Share",color= Color(50,134,22,255), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }})
        },
        bottomBar = {
            Row(modifier= Modifier
                .fillMaxWidth()
                .background(Color.White)){
                Button(onClick = {
                                 if(name=="" || phoneno==""|| housename==""|| locality==""|| city==""){
                                     Toast.makeText(context,"Please fill all the details",Toast.LENGTH_SHORT).show()
                                 }else{
                                     val notificationHelper = NotificationHelper(context)
                                     notificationHelper.createNotificationChannel()
                                     notificationHelper.sendNotification(
                                         "Order Confirmed!",
                                         "See Order Summary"
                                     )
                                    address=housename+" "+locality+" "+city
                                     var order=Orders(name,phoneno,address,orders)
                                    addDetailsToFirebase(order)
                                     clearCartInFirebase()
                                     addOrderToUserDocument(order)
                                     navController.navigate("orderconfirm")
                                 }
                },modifier= Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(55.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(Color(50,134,22,255))) {
                    Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Place Order",color=Color.White, textAlign = TextAlign.Center,fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

    ) {
        Column(modifier= Modifier
            .fillMaxSize()
            .padding(it)) {
            OutlinedTextField(value = name, onValueChange ={
                name=it
            }, label = {
                Text(text = "Name")
            }, singleLine = true ,modifier= Modifier
                .fillMaxWidth()
                .padding(16.dp), keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words)
            )
            OutlinedTextField(value = phoneno, onValueChange ={
                phoneno=it
            }, label = {
                Text(text = "Phone no.")
            }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),modifier= Modifier
                .fillMaxWidth()
                .padding(16.dp))
            OutlinedTextField(value = housename, onValueChange ={
                housename=it
            }, label = {
                Text(text = "House Name/House no.")
            }, singleLine = true,modifier= Modifier
                .fillMaxWidth()
                .padding(16.dp), keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words ))
            OutlinedTextField(value = locality, onValueChange ={
                locality=it
            }, label = {
                Text(text = "Locality/Street Name/Colony Name")
            }, singleLine = true ,modifier= Modifier
                .fillMaxWidth()
                .padding(16.dp), keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words))
            OutlinedTextField(value = city, onValueChange ={
                city=it
            }, label = {
                Text(text = "City/Village")
            }, singleLine = true ,modifier= Modifier
                .fillMaxWidth()
                .padding(16.dp), keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words))

        }
    }
}

fun addDetailsToFirebase(order: Orders) {
    val db = FirebaseFirestore.getInstance()


    val productRef = db.collection("data").document("bLh8HvjILMdoj6JlVBDg")

    // Convert `Orders` object to a Firestore-compatible map
    val orderMap = mapOf(
        "name" to order.name,
        "phoneno" to order.phoneno,
        "address" to order.address,
        "carted" to order.carted.map { cartedItem -> // Convert carted items list to a map
            mapOf(
                "item" to mapOf(
                    "desc" to cartedItem.item.desc,
                    "price" to cartedItem.item.price,
                    "image" to cartedItem.item.image
                ),
                "quantity" to cartedItem.quantity
            )
        }
    )

    // Add to Firestore using `arrayUnion` to keep previous data
    productRef.update("Products", FieldValue.arrayUnion(orderMap))
        .addOnSuccessListener {
            println("Order successfully added to Firestore!")
        }
        .addOnFailureListener { e ->
            // If document doesn't exist, create a new one
            val newOrderData = mapOf("Products" to listOf(orderMap))
            productRef.set(newOrderData)
                .addOnSuccessListener {
                    println("New product order document created!")
                }
                .addOnFailureListener { error ->
                    println("Error adding order: ${error.message}")
                }
        }

}
fun clearCartInFirebase() {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return // Ensure user is authenticated

    val cartRef = db.collection("data").document(userId) // Use UID as document name

    cartRef.update("Cart", emptyList<Any>())
        .addOnSuccessListener {
            println("Cart cleared successfully!")
        }
        .addOnFailureListener { e ->
            println("Error clearing cart: ${e.message}")
        }
}
fun addOrderToUserDocument(order: Orders) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return // Ensure user is authenticated

    val userOrderRef = db.collection("data").document(userId) // Use UID as document name

    // Convert `Orders` object to a Firestore-compatible map
    val orderMap = mapOf(
        "name" to order.name,
        "phoneno" to order.phoneno,
        "address" to order.address,
        "carted" to order.carted.map { cartedItem -> // Convert carted items list to a map
            mapOf(
                "item" to mapOf(
                    "desc" to cartedItem.item.desc,
                    "price" to cartedItem.item.price,
                    "image" to cartedItem.item.image
                ),
                "quantity" to cartedItem.quantity
            )
        }
    )

    // Add to Firestore using `arrayUnion` to keep previous data
    userOrderRef.update("Orders", FieldValue.arrayUnion(orderMap))
        .addOnSuccessListener {
            println("Order successfully added to user's Firestore document!")
        }
        .addOnFailureListener { e ->
            // If document doesn't exist, create a new one
            val newOrderData = mapOf("Orders" to listOf(orderMap))
            userOrderRef.set(newOrderData)
                .addOnSuccessListener {
                    println("New user order document created!")
                }
                .addOnFailureListener { error ->
                    println("Error adding order: ${error.message}")
                }
        }
}



