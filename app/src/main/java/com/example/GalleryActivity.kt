package com.example

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        // Make it edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val recyclerView = findViewById<RecyclerView>(R.id.galleryRecyclerView)
        val specialMessageButton = findViewById<Button>(R.id.specialMessageButton)
        
        // Use a GridLayoutManager with 2 columns
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = GalleryAdapter(this)
        
        specialMessageButton.setOnClickListener {
            val intent = Intent(this, SpecialMessageActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        MusicManager.start(this, R.raw.new_song)
    }
}

class GalleryAdapter(private val context: Context) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private val imageList: List<Int> = listOf(
        R.drawable.ira_1, R.drawable.ira_2, R.drawable.ira_3, R.drawable.ira_4,
        R.drawable.ira_5, R.drawable.ira_6, R.drawable.ira_7, R.drawable.ira_8,
        R.drawable.ira_9, R.drawable.ira_10, R.drawable.ira_11, R.drawable.ira_12,
        R.drawable.ira_13, R.drawable.ira_14, R.drawable.ira_15, R.drawable.ira_16,
        R.drawable.ira_17, R.drawable.ira_18, R.drawable.ira_19, R.drawable.ira_20,
        R.drawable.ira_21, R.drawable.ira_22, R.drawable.ira_23, R.drawable.ira_24,
        R.drawable.ira_25, R.drawable.ira_26, R.drawable.ira_27, R.drawable.ira_28,
        R.drawable.ira_29
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.galleryImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_gallery_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resId = imageList[position]
        holder.imageView.setImageResource(resId)
        
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ImageViewerActivity::class.java)
            intent.putExtra("imageResId", resId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = imageList.size
}
