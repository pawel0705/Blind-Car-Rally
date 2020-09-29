package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class VolumeSoundsLevel : SurfaceView(Settings.CONTEXT), ILevel {
    private var texts: HashMap<String, String> = HashMap()
    private var volume: ArrayList<String> = ArrayList()
    private var volumeIterator = 0

    private var swipe: Boolean = false

    init {
        isFocusable = true

        volume.add("SETTINGS_VOLUME_10")
        volume.add("SETTINGS_VOLUME_20")
        volume.add("SETTINGS_VOLUME_30")
        volume.add("SETTINGS_VOLUME_40")
        volume.add("SETTINGS_VOLUME_50")
        volume.add("SETTINGS_VOLUME_60")
        volume.add("SETTINGS_VOLUME_70")
        volume.add("SETTINGS_VOLUME_80")
        volume.add("SETTINGS_VOLUME_90")
        volume.add("SETTINGS_VOLUME_100")
    }

    override fun initState() {
        texts.putAll(OpenerCSV.readData(R.raw.settings_tts, Settings.languageTTS))

        TextToSpeechManager.speakNow(texts["SETTINGS_SOUNDS_VOLUME"].toString() + texts[volume[Settings.sounds - 1]].toString())
    }

    override fun updateState() {

    }

    override fun destroyState() {

    }

    override fun respondTouchState(event: MotionEvent) {
        when (GestureManager.gestureDetect(event)) {
            GestureType.SWIPE_LEFT -> {
                SoundManager.playSound(R.raw.swoosh)
                volumeIterator++

                if (volumeIterator >= volume.size) {
                    volumeIterator = 0
                }

                Settings.sounds = volumeIterator + 1
                TextToSpeechManager.speakNow(texts["SETTINGS_SOUNDS_VOLUME"].toString() + texts[volume[Settings.sounds - 1]].toString())

                swipe = true
            }
            GestureType.SWIPE_RIGHT -> {
                SoundManager.playSound(R.raw.swoosh)
                volumeIterator--
                if (volumeIterator < 0) {
                    volumeIterator = volume.size - 1
                }

                Settings.sounds = volumeIterator + 1
                TextToSpeechManager.speakNow(texts["SETTINGS_SOUNDS_VOLUME"].toString() + texts[volume[Settings.sounds - 1]].toString())

                swipe = true
            }
            GestureType.DOUBLE_TAP -> {
                SoundManager.playSound(R.raw.accept)

                LevelManager.changeLevel(LevelType.SETTINGS)
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}