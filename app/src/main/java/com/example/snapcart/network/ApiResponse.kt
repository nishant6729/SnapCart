package com.example.snapcart.network

data class ApiResponse(
    val bool: Boolean,
    val request_id: String?,
    val message: List<String>
)
