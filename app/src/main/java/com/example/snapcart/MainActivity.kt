package com.example.snapcart

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.snapcart.ui.theme.DelayClass
import com.example.snapcart.ui.theme.SignIn
import com.example.snapcart.ui.theme.SnapCartTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel by viewModels<DelayClass>()
        super.onCreate(savedInstanceState)

        // Install the splash screen
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !viewModel.isResult.value
            }
        }

        // Initialize FirestoreHelper


        // Check navigation and update the start destination
        lifecycleScope.launch {

                val isUserAuthenticated = FirebaseAuth.getInstance().currentUser != null
                setContent {
                    SnapCartTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val startDestination = if (isUserAuthenticated) "homescreen" else Emailsignin.name

                            // Check if notification click should navigate to OrdersPage
                            val navigateTo = intent.getStringExtra("navigateTo")
                            val finalDestination = if (navigateTo == "orderspage") "orderspage" else startDestination
                            Start(startDestination = finalDestination)
                        }
                    }
                }

        }
    }

}
@Composable
fun Start(startDestination: String) {
    var context= LocalContext.current
    var navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(StartRoute.name) {
            SignIn(navController)
        }
        composable(OTPRoute.name + "/{mobileno}") {
            val mobileno = it.arguments?.getString("mobileno") ?: ""
            OTPVerify(navController, mobileno)
        }
        composable(Emailsignin.name) {
            Emailsignin(navController)
        }
        composable(Emailsignup.name) {
            EmailSignUp(navController)
        }
        composable(mainpage.name + "/{phoneno}" + "/{email}") {
            val phoneno = it.arguments?.getString("phoneno") ?: ""
            val email = it.arguments?.getString("email") ?: ""
            val context = LocalContext.current
            MainPage(navController, phoneno, email, context)
        }
        composable(notavailablepage.name) {
            LocationSelectorScreen(navController = navController)
        }
        composable("homescreen") {

            HomeScreen(navController)
        }
        composable("bestseller_Screen"){
            val subCategories = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<List<SubCategory>>("subCategories")

            if (subCategories != null) {
                BestSellerScreen(navController = navController, bestSeller = subCategories)
            } else {
                // Handle the error case, maybe navigate back or show a message

            }
        }
        composable("checkout"){
            CheckOut(navController)
        }
        composable("AddressPage"){
            AddressPage(navController)
        }
        composable("orderconfirm"){
            OrderConfirm(navController)
        }
        composable("orderspage"){
            OrdersPage(navController)
        }
        composable("categoriespage"){
            CategoriesPage(navController)
        }
        composable(route="itemsofcategory"){
            var subCategory=navController.previousBackStackEntry?.savedStateHandle?.get<SubCategory>("subcategory")
            subCategory?.let {
                ItemsPage(navController,it)
            }

        }
        composable("searchscreen"){
            SearchScreen(navController = navController)
        }
        composable("aftersearchscreen"+"/{searched}"){
            val searched = it.arguments?.getString("searched")?: " "
            AfterSearchScreen(navController,searched)
        }

        
    }
}

