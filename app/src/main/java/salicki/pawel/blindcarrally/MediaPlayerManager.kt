package salicki.pawel.blindcarrally

import android.R

import android.media.MediaPlayer




object MediaPlayerManager {

    lateinit var mediaPlayer: MediaPlayer

    fun initMediaPlayer(soundId: Int){
        mediaPlayer = MediaPlayer.create(Settings.CONTEXT, soundId)
    }

    fun startSound(){
        mediaPlayer.start()
    }

    fun loopSound(){
        mediaPlayer.isLooping = true
    }

    fun unLoopSound(){
        mediaPlayer.isLooping = false
    }

    fun changeVolume(left: Float, right: Float) {
        mediaPlayer.setVolume(left, right)
    }
}