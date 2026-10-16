package com.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var googleSignInClient: GoogleSignInClient? = null

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Log.e("LoginActivity", "Google sign in failed", e)
            Toast.makeText(this, "Sign in failed. Proceeding as guest...", Toast.LENGTH_SHORT).show()
            proceedToNext()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            )
        )
        
        try {
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
            
            // Try to get web client id, will fail if no google-services.json
            val webClientId = resources.getString(
                resources.getIdentifier("default_web_client_id", "string", packageName)
            )
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)
        } catch (e: Exception) {
            Log.w("LoginActivity", "Firebase not fully configured. Defaulting to Guest.", e)
        }

        setContent {
            MyApplicationTheme(dynamicColor = false) {
                var isLoading by remember { mutableStateOf(false) }
                
                LoginScreen(
                    isLoading = isLoading,
                    onSignInClick = {
                        isLoading = true
                        if (googleSignInClient != null) {
                            signInLauncher.launch(googleSignInClient!!.signInIntent)
                        } else {
                            Toast.makeText(this@LoginActivity, "Firebase not configured. Proceeding.", Toast.LENGTH_SHORT).show()
                            proceedToNext()
                        }
                    },
                    onSkipClick = { proceedToNext() }
                )
            }
        }
        
        // Auto-login check
        try {
            if (::auth.isInitialized && auth.currentUser != null) {
                proceedToNext()
            }
        } catch (e: Exception) {}
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userData = hashMapOf(
                            "uid" to it.uid,
                            "email" to it.email,
                            "displayName" to it.displayName,
                            "lastLogin" to System.currentTimeMillis()
                        )
                        firestore.collection("users").document(it.uid)
                            .set(userData)
                            .addOnSuccessListener { proceedToNext() }
                            .addOnFailureListener { proceedToNext() }
                    } ?: proceedToNext()
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    proceedToNext()
                }
            }
    }

    private fun proceedToNext() {
        startActivity(Intent(this, BiometricAuthActivity::class.java))
        finish()
    }
}

@Composable
fun LoginScreen(isLoading: Boolean, onSignInClick: () -> Unit, onSkipClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD1DC)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Welcome",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B4226)
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF6B4226))
            } else {
                Button(
                    onClick = onSignInClick,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Sign in with Google", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onSkipClick) {
                    Text("Skip for now", color = Color(0xFF6B4226))
                }
            }
        }
    }
}
