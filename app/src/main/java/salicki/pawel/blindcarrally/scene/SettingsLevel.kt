package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class SettingsLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var settingsIterator = 0
    private var soundManager: SoundManager = SoundManager()
    private var swipe: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
    }

    private fun initSoundManager(){
        soundManager.initSoundManager()

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.settings_tts, Settings.languageTTS))
    }

    override fun initState() {

        TextToSpeechManager.speakNow(texts["SETTINGS_TUTORIAL"].toString())
        if (Settings.vibrations) {
            TextToSpeechManager.speakQueue(texts["SETTINGS_VIBRATION_ON"].toString())
        } else {
            TextToSpeechManager.speakQueue(texts["SETTINGS_VIBRATION_OFF"].toString())
        }
    }

    override fun updateState(deltaTime: Int) {
        if (swipe) {
            swipe = false
            when (settingsIterator) {
                0 -> {
                    if (Settings.vibrations) {
                        TextToSpeechManager.speakNow(texts["SETTINGS_VIBRATION_ON"].toString())
                    } else {
                        TextToSpeechManager.speakNow(texts["SETTINGS_VIBRATION_OFF"].toString())
                    }
                }
                1 -> {
                    if (Settings.display) {
                        TextToSpeechManager.speakNow(texts["SETTINGS_DISPLAY_ON"].toString())
                    } else {
                        TextToSpeechManager.speakNow(texts["SETTINGS_DISPLAY_OFF"].toString())
                    }
                }
                2 -> {
                    TextToSpeechManager.speakNow(texts["SETTINGS_TTS"].toString())
                }
                3 -> {
                    TextToSpeechManager.speakNow(texts["SETTINGS_SOUNDS"].toString())
                }
                4 -> {
                    TextToSpeechManager.speakNow(texts["SETTINGS_EXIT"].toString())
                }
            }
        }
    }

    override fun destroyState() {
        isFocusable = false

        soundManager.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {

        when (GestureManager.gestureDetect(event)) {
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                settingsIterator++

                if (settingsIterator > 4) {
                    settingsIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                settingsIterator--
                if (settingsIterator < 0) {
                    settingsIterator = 4
                }

                swipe = true
            }
            GestureType.DOUBLE_TAP -> {
                soundManager.playSound(Resources.acceptSound)

                when (settingsIterator) {
                    0 -> {
                        Settings.vibrations = !Settings.vibrations

                        if (Settings.vibrations) {
                            TextToSpeechManager.speakNow(texts["SETTINGS_VIBRATION_ON"].toString())
                        } else {
                            TextToSpeechManager.speakNow(texts["SETTINGS_VIBRATION_OFF"].toString())
                        }
                    }
                    1 -> {
                        Settings.display = !Settings.display

                        if (Settings.display) {
                            TextToSpeechManager.speakNow(texts["SETTINGS_DISPLAY_ON"].toString())
                        } else {
                            TextToSpeechManager.speakNow(texts["SETTINGS_DISPLAY_OFF"].toString())
                        }
                    }
                    2 -> {
                        LevelManager.changeLevel(VolumeTTSLevel())
                    }
                    3 -> {
                        LevelManager.changeLevel(VolumeSoundsLevel())
                    }
                    4 -> {
                        LevelManager.changeLevel(MenuLevel())
                    }
                }
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}