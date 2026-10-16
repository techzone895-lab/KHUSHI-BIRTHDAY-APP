package com.example

import android.content.Context
import android.media.MediaPlayer

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    private var currentResId: Int = 0

    fun getCurrentResId(): Int = currentResId

    fun start(context: Context, resId: Int) {
        if (mediaPlayer != null && currentResId != resId) {
            stopAndRelease()
        }

        if (mediaPlayer == null) {
            currentResId = resId
            mediaPlayer = MediaPlayer.create(context.applicationContext, resId)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } else if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun stopAndRelease() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        currentResId = 0
    }
}
