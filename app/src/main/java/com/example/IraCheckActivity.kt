package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme

class IraCheckActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            )
        )
        
        try {
            MusicManager.start(this, R.raw.dummy_bgm)
        } catch (e: Exception) {}
        
        setContent {
            MyApplicationTheme(dynamicColor = false) {
                IraCheckScreen(
                    onYesClicked = {
                        try {
                            val intent = Intent(this@IraCheckActivity, BirthdaySurpriseActivity::class.java)
                            startActivity(intent)
                            finish()
                        } catch (e: Throwable) {
                            Toast.makeText(this@IraCheckActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun IraCheckScreen(onYesClicked: (() -> Unit)? = null) {
    val context = LocalContext.current
    val pastelPink = Color(0xFFFFD1DC)
    val pastelBlue = Color(0xFFAEC6CF)
    val buttonPink = Color(0xFFFFB6C1)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pastelPink),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.8f),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Kya aap Ira hai?",
                    fontSize = 32.sp,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B4226),
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Button(
                    onClick = {
                        if (onYesClicked != null) {
                            onYesClicked()
                        } else {
                            try {
                                val activity = context as? android.app.Activity
                                val intent = Intent(context, BirthdaySurpriseActivity::class.java)
                                if (activity != null) {
                                    activity.startActivity(intent)
                                    activity.finish()
                                } else {
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(intent)
                                }
                            } catch (e: Throwable) {
                                Toast.makeText(context, "Nav Error: ${e.message}", Toast.LENGTH_LONG).show()
                                e.printStackTrace()
                            }
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = pastelBlue),
                    modifier = Modifier.size(width = 120.dp, height = 56.dp)
                ) {
                    Text("Yes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                }
                
                Button(
                    onClick = {
                        Toast.makeText(context, "Are malkin aaj to jhote mt bolo", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonPink),
                    modifier = Modifier.size(width = 120.dp, height = 56.dp)
                ) {
                    Text("No", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                }
            }
        }
    }
}
