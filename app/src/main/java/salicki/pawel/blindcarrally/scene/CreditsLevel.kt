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
    private var SoundManager: SoundManager = SoundManager()
    init {
        SoundManager.initSoundManager()
        isFocusable = true
    }

    override fun initState() {
        texts.putAll(OpenerCSV.readData(R.raw.credits_tts, Settings.languageTTS))

        TextToSpeechManager.speakNow(texts["CREDITS_AUTHOR"].toString())
        TextToSpeechManager.speakQueue(texts["CREDITS_BACK"].toString())
    }

    override fun updateState(deltaTime: Int) {

    }

    override fun destroyState() {

    }

    override fun respondTouchState(event: MotionEvent) {
        when (GestureManager.gestureDetect(event)) {
            GestureType.DOUBLE_TAP -> {
                SoundManager.playSound(R.raw.accept)
                LevelManager.changeLevel(LevelType.MENU)
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}