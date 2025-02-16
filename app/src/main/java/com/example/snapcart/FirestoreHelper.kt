package com.example.snapcart

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CountDownLatch

class FirestoreHelper {

    // Function to store the user object in Firestore
    fun storeUserData(user: users, context: Context) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val docRef = db.collection("data").document(userId)

            // Use set with merge to avoid losing previous data
            docRef.set(user, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(context, "Successfully added/updated user", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error in adding/updating user: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    fun fetchAvailableCities(callback: (List<String>?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("data").document("1ZfgFwOhuk5Vvc6izgnG")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val cities = document.get("Available cities") as? List<String>
                    callback(cities)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
    fun updateCityField(newCity: String, context: Context) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        // Reference to the user's document in Firestore
        if(userId!=null){
            val docRef = db.collection("data").document(userId)

            // Update the "city" field in the document
            docRef.update("city", newCity)
                .addOnSuccessListener {
                    // Successfully updated the city
                    Toast.makeText(
                        context,
                        "Successfully updated city to $newCity",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    // Error occurred while updating the city
                    Toast.makeText(
                        context,
                        "Error in updating city: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
        else{
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
    fun checkUserNavigation(callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("data").document("vEuS569lmtX2fsyWprKt")

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val phone = document.getString("phoneno")
                    val email = document.getString("email")

                    // If either phone or email is not empty, navigate to the homepage
                    if (!phone.isNullOrEmpty() || !email.isNullOrEmpty()) {
                        callback(true) // Navigate to Home
                    } else {
                        callback(false) // Navigate to SignIn
                    }
                } else {
                    callback(false) // Navigate to SignIn if document does not exist
                }
            }
            .addOnFailureListener { e ->
                callback(false) // Handle errors gracefully
            }
    }
    fun logoutUser(context: Context, callback: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val docRef = db.collection("data").document(userId)

            // Clear fields in Firestore before logging out
            docRef.update(mapOf("phoneno" to "", "email" to ""))
                .addOnSuccessListener {
                    // Sign out the user from Firebase Authentication
                    auth.signOut()
                    Toast.makeText(context, "Successfully logged out", Toast.LENGTH_SHORT).show()
                    callback(true) // Trigger navigation to EmailSignIn screen
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error during logout: ${e.message}", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
        } else {
            // Directly sign out if userId is null (edge case)
            auth.signOut()
            callback(true) // Navigate to EmailSignIn screen
        }
    }

//    fun fetchSubCategories(): List<SubCategory> {
//        val firestore = FirebaseFirestore.getInstance()
//        val collectionName = "data"
//        val documentId = "1ZfgFwOhuk5Vvc6izgnG"
//        val latch = CountDownLatch(1)
//        var subCategories: List<SubCategory> = emptyList()
//
//        firestore.collection(collectionName).document(documentId).get()
//            .addOnSuccessListener { documentSnapshot ->
//                val categories = documentSnapshot.get("categories") as? List<Map<String, Any>>
//                subCategories = categories?.drop(1)?.map { map ->
//                    val name = map["name"] as? String ?: ""
//                    val itemsList = (map["items"] as? List<Map<String, Any>>)?.map { itemMap ->
//                        Items(
//                            weight = itemMap["weight"] as? String ?: "",
//                            flavour = itemMap["flavour"] as? String ?: "",
//                            desc = itemMap["desc"] as? String ?: "",
//                            image = itemMap["image"] as? String ?: "",
//                            offers = itemMap["offers"] as? String ?: "",
//                            price = itemMap["price"] as? String ?: "",
//                            rating = itemMap["rating"] as? String ?: ""
//                        )
//                    } ?: emptyList()
//
//                    SubCategory(name = name, items = itemsList)
//                } ?: emptyList()
//
//                latch.countDown() // Release the latch
//            }
//            .addOnFailureListener { exception ->
//                exception.printStackTrace()
//                latch.countDown() // Release the latch in case of error
//            }
//
//        latch.await() // Wait until Firestore operation completes
//        return subCategories
//    }


}