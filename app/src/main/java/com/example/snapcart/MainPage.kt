
package com.example.snapcart
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi



@OptIn(UnstableApi::class)
@Composable
fun MainPage(
    navController: NavController,
    phoneno: String,
    email: String,
    context: Context
) {
    var avbCities by remember { mutableStateOf<List<String>?>(null) }
    val firestoreHelper = FirestoreHelper()
    val locationViewModel: LocationViewModel = viewModel()

    // State to track when navigation is ready
    var isNavigationReady by remember { mutableStateOf(false) }

    // Observing city and address from LocationViewModel
    val city by locationViewModel.city.collectAsState(initial = null)
    var check by remember {
        mutableStateOf(false)
    }
    // Permission launcher to request location permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationViewModel.fetchCity(context)
            check=true
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }



    // Automatically request permission on this screen
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        // Fetch available cities
        firestoreHelper.fetchAvailableCities { cities ->
            avbCities = cities
            Log.d("m tag", "data fetched")
            isNavigationReady = true // Set navigation readiness when cities are fetched
        }
    }

    // Navigate when both city and available cities are ready
    LaunchedEffect(isNavigationReady, city) {
        if (isNavigationReady && city != null) {
            val user = users(phoneno, email, city ?: "")
            firestoreHelper.storeUserData(user, context)
            if (avbCities?.contains(user.city) == true) {
                // Store user data
                navController.navigate("homescreen") // Navigate to home screen
            } else {
                navController.navigate(notavailablepage.name) // Navigate to not available page
            }
        }
    }
    if(city!=null){
        androidx.compose.material3.Text(text = "Abhi tak to thik hai")
    }
    // UI to display loading indicator or navigate
    if (!isNavigationReady || city == null) {
        // Display loading indicator while data is being fetched
        Column(
            modifier = Modifier
                .fillMaxSize()
                .height(100.dp)
                .size(100.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading, please wait...",
                fontSize = 16.sp
            )
        }
    } else {
        // Once data is available, the UI for navigation will be handled by LaunchedEffect
        Text(
            text = "Ready to navigate!",
            fontSize = 16.sp
        )
    }
}

