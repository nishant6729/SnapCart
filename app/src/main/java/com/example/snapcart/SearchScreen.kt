package com.example.snapcart

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box


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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController){
    var isLoading by remember { mutableStateOf(true) }
    var searched by remember{ mutableStateOf("") }
    var recentsearches by remember{ mutableStateOf<List<String>>(emptyList()) }
    val topsearches: List<String> = listOf("Kurkure", "Banana", "Coca Cola","Tomatos","Thumbs Up","Onion")
    val coroutine= rememberCoroutineScope()
    var itemslist by remember {
        mutableStateOf<List<Items>> (emptyList())
    }
    var filteredlist by remember {
        mutableStateOf<List<Items>> (emptyList())
    }
    var cart by remember { mutableStateOf<List<Carted>>(emptyList()) }
    LaunchedEffect(Unit){
        recentsearches=fetchRecentSearches()
        isLoading = false
        itemslist=fetchStoredItems()
        cart=fetchCartFromFirebase()
    }
    Scaffold(topBar = {
            TopAppBar(title = {

                SearchTextField(
                    searched = searched,
                    onSearchChange = { searched = it },
                    onSearch = { navController.navigate("aftersearchscreen/$searched")
                        coroutine.launch {
                            addSearchToRecent(searched)
                        }
                               },
                    navController = navController
                )


            }, colors = TopAppBarDefaults.topAppBarColors(Color(250,247,232,255)) )
    }, bottomBar = {
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(end = 8.dp).background(Color.White), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
            Row(modifier= Modifier
                .wrapContentWidth()
                .padding(16.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                Image(painter = painterResource(id = R.drawable.discount), contentDescription =null,modifier=Modifier.size(30.dp) )
                Spacer(modifier=Modifier.width(8.dp))
                Column(modifier=Modifier.wrapContentHeight()) {
                    Text("Get Flat ₹50 OFF", fontWeight = FontWeight.SemiBold,color= Color(38,111,227,255), fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Add items worth ₹186 more", fontWeight = FontWeight.Light, fontSize = 13.sp)
                }
            }
            Icon(painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24), contentDescription =null ,modifier=Modifier.padding(end=4.dp))

        }
    }) {
        Box(modifier=Modifier.fillMaxSize())
        {
            LazyColumn(modifier = Modifier.padding(it)) {
                item {
                    when {
                        isLoading -> {
                            // Show a loading indicator
                            Text("Loading...")
                        }

                        recentsearches.isNotEmpty() -> {
                            SearchDialog(recentsearches, "Recent Searches", onclick = {
                                searched = it

                                coroutine.launch {
                                    addSearchToRecent(searched)
                                }
                            })

                        }

                        else -> {
                            SearchDialog(topsearches, "Top Searches", onclick = {
                                searched = it

                                coroutine.launch {
                                    addSearchToRecent(searched)
                                }
                            })
                        }
                    }
                }
            }
        }
        if(searched.isNotEmpty()){
            filteredlist=itemslist
                .filter {
                    it.desc.contains(searched, ignoreCase = true) }
                .sortedBy { it.desc }

            Box(modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp)
                .background(
                    Color(250,247,232,255))
                 ){

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
fun SearchDialog(searches: List<String>, name: String,onclick: (String)->Unit) {
    var coroutine= rememberCoroutineScope()
    Column {
        Row (modifier= Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            if(name=="Recent Searches"){
                TextButton(onClick = {coroutine.launch {
                    clearRecentSearches()
                }
                    }) {
                    Text("clear",color=Color(50,134,22,255), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

        }
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            if(searches.size>=1) SearchCard(searches[0],onclick)
            if(searches.size>=2) SearchCard(searches[1],onclick)
            if(searches.size>=3) SearchCard(searches[2],onclick)
        }
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(16.dp),horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            if(searches.size>=4) SearchCard(searches[3],onclick)
            if(searches.size>=5) SearchCard(searches[4],onclick)
            if(searches.size>=6) SearchCard(searches[5],onclick)
        }

    }
}

@Composable
fun SearchCard(name: String,onclick:(String)->Unit) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(10.dp)
            .clickable { onclick(name) },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Gray),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_search_24),
                contentDescription = null,
                tint = Color.Gray, modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }


}


suspend fun fetchRecentSearches(): List<String> {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("data").document(userId)

    return try {
        val document = userRef.get().await()
        if (document.exists() && document.contains("Recent")) {
            document.get("Recent") as? List<String> ?: emptyList()
        } else {
            // If the field is missing, create it in Firestore
            userRef.update("Recent", emptyList<String>()).await()
            emptyList()
        }
    } catch (e: Exception) {
        emptyList() // Return an empty list in case of an error
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    searched: String,
    onSearchChange: (String) -> Unit,
    onSearch: () -> Unit,
    navController: NavController // if you need it here, otherwise handle navigation elsewhere
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = searched,
        onValueChange = { onSearchChange(it) },
        textStyle = TextStyle(fontSize = 16.sp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                // Optionally hide the keyboard
                keyboardController?.hide()
                onSearch()
            }
        ),
        leadingIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        },
        trailingIcon = {
            IconButton(onClick = {
                // Trigger mic operation if needed
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_mic_24),
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        },
        placeholder = {
            Text(
                text = "Search for atta, dal, coke and more",
                fontWeight = FontWeight.Light,
                fontSize = 15.sp
            )
        },
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

suspend fun addSearchToRecent(searchTerm: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("data").document(userId)

    try {
        // Fetch the current document snapshot
        val docSnapshot = userRef.get().await()
        // Retrieve the current "Recent" list, or initialize an empty mutable list if it doesn't exist
        var recentList: MutableList<String> = if (docSnapshot.exists() && docSnapshot.contains("Recent")) {
            (docSnapshot.get("Recent") as? List<String>)?.toMutableList() ?: mutableListOf()
        } else {
            mutableListOf()
        }

        // Remove the searchTerm if it already exists to avoid duplicates
        recentList.remove(searchTerm)
        // Insert the new search term at the beginning (stack effect)
        recentList.add(0, searchTerm)

        // If the list size exceeds 6, remove extra items from the end
        if (recentList.size > 6) {
            recentList = recentList.take(6).toMutableList()
        }

        // Update Firestore with the new "Recent" list using merge to avoid overwriting other fields
        userRef.set(mapOf("Recent" to recentList), SetOptions.merge()).await()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
suspend fun fetchStoredItems(): List<Items> {
    return withContext(Dispatchers.IO) {
        val firestore = FirebaseFirestore.getInstance()
        val targetDocumentId = "itemsList" // Document where items are stored
        try {
            val documentSnapshot = firestore.collection("items").document(targetDocumentId).get().await()
            val itemsList = documentSnapshot.get("items") as? List<Map<String, Any>>

            itemsList?.map { itemMap ->
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
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Firestore", "Error fetching stored items: ${e.message}")
            emptyList()
        }
    }
}
suspend fun clearRecentSearches() {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("data").document(userId)

    try {
        // Update Firestore by setting "Recent" to an empty list
        userRef.update("Recent", emptyList<String>()).await()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

