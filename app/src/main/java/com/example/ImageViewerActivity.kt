package com.example

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat

class ImageViewerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        // Make it edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val imageView = findViewById<ImageView>(R.id.fullImageView)
        val closeButton = findViewById<ImageButton>(R.id.closeButton)

        val imageResId = intent.getIntExtra("imageResId", 0)
        if (imageResId != 0) {
            imageView.setImageResource(imageResId)
        }

        closeButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        MusicManager.start(this, R.raw.new_song)
    }
}
