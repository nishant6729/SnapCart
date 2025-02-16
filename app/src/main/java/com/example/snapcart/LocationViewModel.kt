package com.example.snapcart

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    private val _city = MutableStateFlow<String?>(null)
    val city: StateFlow<String?> = _city
    private val _address = MutableStateFlow<String?>(null)
    val address: StateFlow<String?> = _address

    fun fetchCity(context: Context) {
        viewModelScope.launch {
            val fetchedCity = LocationHelper.fetchCity(context)

            _city.value = fetchedCity ?: "Ranpur" // Default value assigned here
        }
    }
    fun fetchAddress(context: Context) {
        viewModelScope.launch {
            val fetchedAddress = LocationHelper.fetchAddressAsString(context)
            _address.value = fetchedAddress
        }
    }
}