package com.example.snapcart

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import java.util.Locale

object LocationHelper {


    suspend fun fetchCity(context: Context): String? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val location: Location? = fusedLocationClient.lastLocation.await()
        location?.let {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            return addresses?.firstOrNull()?.locality
        }
        return null

    }
    suspend fun fetchAddressAsString(context: Context): String? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        // Get the last known location
        val location: Location? = fusedLocationClient.lastLocation.await()

        // Check if location is not null
        location?.let {
            val latitude = it.latitude
            val longitude = it.longitude

            // Use Geocoder to fetch the address
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            // Return the first address found as a string
            return addresses?.firstOrNull()?.getAddressLine(0)
        }

        return null
    }
}