package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class CalibrationLevel: SurfaceView(Settings.CONTEXT), ILevel  {
    private var texts: HashMap<String, String> = HashMap()

    init {
        isFocusable = true
    }

    override fun initState() {
        texts.putAll(OpenerCSV.readData(R.raw.calibration_tts, Settings.languageTTS))

        TextToSpeechManager.speakNow(texts["CALIBRATION_TUTORIAL"].toString())
    }

    override fun updateState() {

    }

    override fun destroyState() {

    }

    override fun respondTouchState(event: MotionEvent) {
        when (GestureManager.gestureDetect(event)) {
            GestureType.DOUBLE_TAP -> {
                MovementManager.register()
                SoundManager.playSound(R.raw.accept)
                LevelManager.changeLevel(LevelType.GAME)
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}