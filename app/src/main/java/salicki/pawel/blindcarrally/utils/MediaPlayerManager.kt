package salicki.pawel.blindcarrally.utils

import android.media.MediaPlayer
import salicki.pawel.blindcarrally.information.Settings

object MediaPlayerManager {

    var mediaPlayer: MediaPlayer = MediaPlayer()

    fun initMediaPlayer(soundId: Int) {
        mediaPlayer = MediaPlayer.create(
            Settings.CONTEXT, soundId
        )
    }

    fun startSound() {

        mediaPlayer.start()

    }

    fun loopSound() {

        mediaPlayer.isLooping = true

    }

    fun unLoopSound() {

        mediaPlayer.isLooping = false

    }

    fun stopSound() {

        mediaPlayer.stop()

    }

    fun changeVolume(left: Float, right: Float) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(left, right)
        }
    }
}