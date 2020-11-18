package salicki.pawel.blindcarrally.utils

import android.media.MediaPlayer
import salicki.pawel.blindcarrally.information.Settings

object MediaPlayerManager {

    lateinit var mediaPlayer: MediaPlayer

    fun initMediaPlayer(soundId: Int){
        mediaPlayer = MediaPlayer.create(
            Settings.CONTEXT, soundId)
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