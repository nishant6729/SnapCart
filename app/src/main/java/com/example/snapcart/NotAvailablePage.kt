package com.example.snapcart

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectorScreen(navController: NavController) {
    val firestoreHelper=FirestoreHelper()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    // Show bottom sheet if needed
    if (isBottomSheetVisible) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    isBottomSheetVisible = false
                }
            }
            , containerColor = Color(239, 242, 251, 255)
        ) {
            LocationBottomSheetContent(
                onSetLocationManually = {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                        isBottomSheetVisible = false
                    }
                }, firestoreHelper = firestoreHelper, navController = navController

            )
        }
    }

    // Main content of the screen
    NotAvailable(navController) {
        coroutineScope.launch {
            isBottomSheetVisible = true
        }
    }
}

@Composable
fun LocationBottomSheetContent(
    firestoreHelper: FirestoreHelper,
    onSetLocationManually: () -> Unit,navController: NavController
) {
    var avbCities by remember { mutableStateOf<List<String>>(emptyList()) }
    var searched by remember { mutableStateOf("") }
    var filteredCities by remember { mutableStateOf<List<String>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    // Fetch available cities when the composable is composed
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            firestoreHelper.fetchAvailableCities { cities ->
                if (cities != null) {
                    avbCities = cities
                }
                if (cities != null) {
                    filteredCities = cities
                } // Initially display all cities
            }
        }
    }
    Text(
        text = "Select Location",
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.Black,
        modifier = Modifier.padding(16.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .padding(16.dp)
    ) {


        OutlinedTextField(value = searched, onValueChange ={ query ->
            searched = query
            filteredCities = avbCities.filter { city ->
                city.startsWith(query, ignoreCase = true)
            }
        },modifier= Modifier
            .fillMaxWidth()

            .shadow(
                8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            ), shape = RoundedCornerShape(16.dp),
            singleLine = true, colors = TextFieldDefaults.colors(focusedContainerColor = Color.White , unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.White, unfocusedIndicatorColor = Color.White)
        , placeholder = {Text("Search for your City", fontWeight = FontWeight.Light)}, leadingIcon = {
            Icon(painter = painterResource(id = R.drawable.baseline_search_24), contentDescription = null, tint = Color(50,132,24,255))
            })


        Spacer(modifier = Modifier.height(8.dp))
        if(!searched.isEmpty())
        // Display Filtered Cities
        {
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)) {
                items(filteredCities) { city ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .padding(4.dp)
                            .clickable {
                                searched = city // Set the clicked city as the search text
                            },
                        colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ){
                       Row(verticalAlignment = Alignment.CenterVertically,modifier=Modifier.fillMaxWidth()){
                           Text(text = city,color=Color.Black, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                       }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(4.dp),
            colors = CardDefaults.cardColors(Color.White),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Zomato Logo Image
                Image(
                    painter = painterResource(id = R.drawable.zomato),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp) // Adjust size for better visibility
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))

                // Text Content
                Column(
                    modifier = Modifier.weight(1f) // Push the text to occupy remaining space
                ) {
                    Text(
                        text = "Import your addresses from Zomato",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Get your saved address in a single click",
                        fontWeight = FontWeight.Light,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                // Import Button
                TextButton(
                    onClick = { /* TODO: Handle Import Action */ },
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "Import",
                        fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        color = Color(50, 132, 24, 255)
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                      if(avbCities.contains(searched)){
                          firestoreHelper.updateCityField(searched,context)
                          navController.navigate("homescreen")
                      }else{
                          Toast.makeText(context,"Service not available at searched location",Toast.LENGTH_SHORT)
                      }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 8.dp), colors = ButtonDefaults.buttonColors(Color(
                50,
                130,
                24,
                255
            )
            )
        ) {
            Text("Set Location Manually")
        }
    }
}


@Composable
fun NotAvailable(navController: NavController, onSetLocationManually: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.notavailable),
                contentDescription = null,
                modifier = Modifier.height(300.dp),
                alignment = Alignment.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Not available",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Sorry SnapCart is not available at your current location yet.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "We will be there soon - hang on tight!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onSetLocationManually,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color(50, 134, 22, 255))
                ) {
                    Text(
                        text = "Set Location Manually",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
