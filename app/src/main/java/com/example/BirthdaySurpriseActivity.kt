package com.example

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme

class BirthdaySurpriseActivity : ComponentActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private val navigateRunnable = Runnable {
        try {
            startActivity(Intent(this@BirthdaySurpriseActivity, MainActivity::class.java))
            finish()
        } catch (e: Throwable) {
            android.widget.Toast.makeText(this@BirthdaySurpriseActivity, "Runnable Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
                ),
                navigationBarStyle = SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
                )
            )
            
            handler.postDelayed(navigateRunnable, 4000)
    
            setContent {
                MyApplicationTheme(dynamicColor = false) {
                    BirthdaySurpriseScreen()
                }
            }
        } catch (e: Throwable) {
            android.widget.Toast.makeText(this, "Surprise Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(navigateRunnable)
    }
}

@Composable
fun BirthdaySurpriseScreen() {
    val pastelPink = Color(0xFFFFF0F5)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pastelPink),
        contentAlignment = Alignment.Center
    ) {
        FloatingBalloonsAnimation()
        
        Surface(
            color = Color.White.copy(alpha = 0.8f),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "O my gosh malkin this is 3rd July its is your birthday!",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF800020),
                textAlign = TextAlign.Center,
                letterSpacing = 0.08.em,
                lineHeight = 50.sp,
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}
