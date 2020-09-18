package salicki.pawel.blindcarrally

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build


object SoundManager {
    private var soundPool: SoundPool? = null
    private val MAX_STREAMS = 2

    private var sound1: Int = 0
    private var sound2: Int = 0

    fun initSoundManager(){
        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            SoundPool(MAX_STREAMS, AudioManager.STREAM_ACCESSIBILITY, 0)
        }

        sound1  = soundPool?.load(Settings.CONTEXT, R.raw.swoosh, 1)!!
        sound2  = soundPool?.load(Settings.CONTEXT, R.raw.accept, 1)!!
    }

    fun playSound(soundId: Int){
        when (soundId) {
            R.raw.swoosh -> {
                soundPool!!.play(sound1, 1f, 1f, 0, 0, 1f)
            }
            R.raw.accept -> {
                soundPool!!.play(sound2, 1f, 1f, 0, 0, 1f)
            }

        }
    }

    fun destroy(){
        if(soundPool != null){
            soundPool?.release()
            soundPool = null
        }
    }
}

