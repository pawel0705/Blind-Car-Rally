package salicki.pawel.blindcarrally

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build


class SoundManager {
    private var soundPool: SoundPool? = null
    private val maxStrams = 10

    private var sounds: MutableMap<Int, Int> = mutableMapOf<Int, Int>()

    fun initSoundManager() {
        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            SoundPool.Builder()
                .setMaxStreams(maxStrams)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            SoundPool(maxStrams, AudioManager.STREAM_ACCESSIBILITY, 0)
        }
    }

    fun addSound(soundId: Int) {
        sounds?.set(soundId, soundPool?.load(Settings.CONTEXT, soundId, 1)!!)
    }

    fun playSound(soundId: Int, leftVolume: Float = 1F, rightVolume: Float = 1F, loop: Int = 0, rate: Float = 1F) {
        if (soundPool != null) {
            sounds?.get(soundId)?.let {
                soundPool!!.play(
                    it,
                    Settings.sounds * 0.1F * leftVolume,
                    Settings.sounds * 0.1F * rightVolume,
                    0,
                    loop,
                    rate
                )
            }
        }
    }

    fun destroy() {
        if (soundPool != null) {
            soundPool?.release()
            soundPool = null
        }
    }
}

