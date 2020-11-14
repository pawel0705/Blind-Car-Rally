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
    private var lastOption = 0
    private var soundManager: SoundManager = SoundManager()
    private var swipe: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initVolumeOptions()
    }

    private fun initVolumeOptions() {
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
        TextToSpeechManager.speakNow(texts["SETTINGS_SOUNDS_VOLUME"].toString() + texts[volume[Settings.sounds - 1]].toString())
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.settings_tts, Settings.languageTTS))
    }

    override fun updateState(deltaTime: Int) {
        if(volumeIterator != lastOption){

            TextToSpeechManager.speakNow(texts["SETTINGS_SOUNDS_VOLUME"].toString() + texts[volume[Settings.sounds - 1]].toString())

            lastOption = volumeIterator
        }
    }

    override fun destroyState() {
        isFocusable = false

        soundManager.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {
        swipe = false

        when(GestureManager.swipeDetect(event)){
            GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                volumeIterator++

                if (volumeIterator >= volume.size) {
                    volumeIterator = 0
                }

                Settings.sounds = volumeIterator + 1

                SharedPreferencesManager.saveConfiguration("sounds", Settings.sounds.toString())

                TextToSpeechManager.speakNow(texts["SETTINGS_SOUNDS_VOLUME"].toString() + texts[volume[Settings.sounds - 1]].toString())

                swipe = true
            }
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                volumeIterator--
                if (volumeIterator < 0) {
                    volumeIterator = volume.size - 1
                }

                Settings.sounds = volumeIterator + 1

                SharedPreferencesManager.saveConfiguration("sounds", Settings.sounds.toString())

                TextToSpeechManager.speakNow(texts["SETTINGS_SOUNDS_VOLUME"].toString() + texts[volume[Settings.sounds - 1]].toString())

                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureType.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(Resources.acceptSound)

                LevelManager.changeLevel(SettingsLevel())
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 10 -> {
                    volumeIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 2 -> {
                    volumeIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 3 -> {
                    volumeIterator = 2
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 4 -> {
                    volumeIterator = 3
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 5 -> {
                    volumeIterator = 4
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 6 -> {
                    volumeIterator = 5
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 7 -> {
                    volumeIterator = 6
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 8 -> {
                    volumeIterator = 7
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 9 -> {
                    volumeIterator = 8
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 10 -> {
                    volumeIterator = 9
                }
            }
            Settings.sounds = volumeIterator + 1

            SharedPreferencesManager.saveConfiguration("sounds", Settings.sounds.toString())
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}