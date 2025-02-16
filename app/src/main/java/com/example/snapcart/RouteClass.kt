package com.example.snapcart

open class RouteClass(val name: String)

object StartRoute : RouteClass("login")
object OTPRoute:RouteClass("OTPVerification")
object Emailsignin:RouteClass("Emailsignin")
object Emailsignup:RouteClass("Emailsignup")
object mainpage:RouteClass("Mainpage")
object notavailablepage:RouteClass("NotAvailable")
data class Product(
    val productId: String = "",
    val name: String = "",
    val price: String = "",
    val category: String = ""
)
data class User(
    val userId: String? = null,
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)



