package com.example.snapcart

import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth

object Utils {

    private var FirebaseAuthInstance: FirebaseAuth?=null
    fun getAuthInstance(): FirebaseAuth {
        if(FirebaseAuthInstance==null){
            FirebaseAuthInstance= FirebaseAuth.getInstance()
        }
        return FirebaseAuthInstance as FirebaseAuth
    }
}