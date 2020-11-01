package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class CreditsLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var soundManager: SoundManager = SoundManager()

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(Resources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.credits_tts, Settings.languageTTS))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["CREDITS_AUTHOR"].toString())
        TextToSpeechManager.speakQueue(texts["CREDITS_BACK"].toString())
    }

    override fun updateState(deltaTime: Int) {

    }

    override fun destroyState() {
        isFocusable = false

        soundManager.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {
        when (GestureManager.doubleTapDetect(event)) {
            GestureType.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(Resources.acceptSound)
                LevelManager.changeLevel(MenuLevel())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}