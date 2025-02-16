package com.example.snapcart

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.snapcart.network.RetrofitClient
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.awaitResponse
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {
    // State for authentication status
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }









    // Firebase Email Sign-up
    fun signUpWithEmail(email: String, password: String, context: Context, navController: NavController) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Email or password cannot be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        _authState.value = AuthState.Loading

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("Sign-up successful!")
                    Toast.makeText(context, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                    val phoneno=""
                    navController.navigate(mainpage.name+"/$phoneno"+"/$email") // Navigate to main page
                } else {
                    val exception = task.exception
                    _authState.value = AuthState.Error(exception?.localizedMessage ?: "Unknown error occurred")
                    Toast.makeText(context, "Sign-up failed: ${exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Firebase Email Sign-in
    fun signInWithEmail(email: String, password: String, activity: Activity, onResult: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Log.d("Auth", "signInWithEmail: success")
                    onResult(true) // Sign-in success
                } else {
                    Log.w("Auth", "signInWithEmail: failure", task.exception)
                    onResult(false) // Sign-in failure
                }
            }
    }

    // Authentication states
    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val message: String) : AuthState()
        data class Error(val error: String) : AuthState()
    }

}
