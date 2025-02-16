package com.example.snapcart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class users(val phoneno:String="", val email:String="", var city:String?="")
@Parcelize
data class Items(
    val weight: String = "",
    val flavour: String = "",
    val desc: String = "",
    val image: String = "",
    val offers: String = "",
    val price: String = "",
    val rating: String = ""
) : Parcelable

@Parcelize
data class SubCategory(
    val name: String = "",
    val items: List<Items> = emptyList()
) : Parcelable

@Parcelize
data class SubCategories(
    val name: String = "",
    val subcategory: List<SubCategory> = emptyList()
) : Parcelable
@Parcelize
data class Carted(
    val item:Items= Items(),
    var quantity: Int =0
) : Parcelable
data class Orders(
    val name:String="",
    val phoneno:String="",
    val address:String="",
    val carted:List<Carted> = emptyList()
)

