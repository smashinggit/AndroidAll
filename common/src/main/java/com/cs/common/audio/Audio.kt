package com.cs.common.audio

import android.content.Context
import android.media.MediaPlayer

object Audio {

    private var mMediaPlayer: MediaPlayer? = null

    fun playAssetsFile(context: Context, fileName: String) {
        try {
            stop()

            val assetFileDescriptor = context.assets.openFd(fileName)
            mMediaPlayer = MediaPlayer()
            mMediaPlayer?.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.declaredLength
            )

            mMediaPlayer?.setOnPreparedListener {
                mMediaPlayer?.start()
                mMediaPlayer?.isLooping = true
            }
            mMediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer?.stop()
            mMediaPlayer?.reset()
            mMediaPlayer?.release()
            mMediaPlayer = null
        }
    }
}