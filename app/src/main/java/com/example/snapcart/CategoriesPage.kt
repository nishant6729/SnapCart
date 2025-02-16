package com.example.snapcart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesPage(navController: NavController){
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var context= LocalContext.current
    var firestoreHelper=FirestoreHelper()
    var Subcategories by remember { mutableStateOf<List<SubCategory>>(emptyList()) }
    var selected by remember { mutableStateOf("categories") }
    LaunchedEffect(Unit){
        Subcategories = fetchSubCategories()
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
    ){
        Column(modifier=Modifier.fillMaxSize().padding(it).background(
            Brush.horizontalGradient(
                colors = listOf(
                    Color(236, 189, 49, 255), // Gradient start color

                    Color(246, 210, 100, 255),
                    Color(236, 188, 44, 255)// Gradient end color
                )
            )
        )){
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp).nestedScroll(scrollBehavior.nestedScrollConnection)

            ) {
                items(Subcategories) {
                    CategoriesCard(it.name, it.items,navController,it)
                }
                items(Subcategories) {
                    CategoriesCard(it.name, it.items,navController,it)
                }
                items(Subcategories) {
                    CategoriesCard(it.name, it.items,navController,it)
                }

            }

        }
    }

}

@Composable
fun CategoriesCard(name: String, items: List<Items>,navController: NavController,subCategory: SubCategory) {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(16.dp), border = BorderStroke(1.dp,
        Color.Gray), modifier = Modifier.height(180.dp).clickable {
            navController.currentBackStackEntry?.savedStateHandle?.set("subcategory",subCategory)
            navController.navigate("itemsofcategory")
    }
    ){
        Column(){
            Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,modifier=Modifier.padding(8.dp))
            Spacer(modifier=Modifier.height(16.dp))
            Image(painter = rememberImagePainter(data = items[0].image), contentDescription = null,modifier=Modifier.fillMaxWidth())
        }

    }
}