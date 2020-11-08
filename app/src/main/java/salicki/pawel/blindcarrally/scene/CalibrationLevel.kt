package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.os.Handler
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.timerTask

class CalibrationLevel : SurfaceView(Settings.CONTEXT), ILevel {
    private var textsCalibration: HashMap<String, String> = HashMap()
    private var soundManager: SoundManager = SoundManager()
    private var soundManagerEar: SoundManager = SoundManager()

    private var time = 0
    private var timeSeconds = 0

    private var leftEar: Boolean = false
    private var rightEar: Boolean = false
    private var wheel: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()
        soundManagerEar.initSoundManager()

        soundManager.addSound(Resources.acceptSound)
        soundManagerEar.addSound(R.raw.beep)
    }

    private fun readTTSTextFile() {
        textsCalibration.putAll(OpenerCSV.readData(R.raw.calibration_tts, Settings.languageTTS))
    }

    override fun initState() {

    }

    override fun updateState(deltaTime: Int) {
        time++

        if (time % 30 == 0) {
            timeSeconds++
        }

        if (!TextToSpeechManager.isSpeaking() && !leftEar) {
            TextToSpeechManager.speakNow(textsCalibration["CALIBRATION_LEFT"].toString())
            Timer().schedule(timerTask {
                soundManagerEar.playSound(R.raw.beep, 0.6F, 0F, 3)
            }, 1000)
            leftEar = true
        } else if (!TextToSpeechManager.isSpeaking() && !rightEar) {
            TextToSpeechManager.speakNow(textsCalibration["CALIBRATION_RIGHT"].toString())
            Timer().schedule(timerTask {
                soundManagerEar.playSound(R.raw.beep, 0F, 0.6F, 3)
            }, 1000)
            rightEar = true
        } else if (!TextToSpeechManager.isSpeaking() && leftEar && rightEar && !wheel) {
            TextToSpeechManager.speakNow(textsCalibration["CALIBRATION_TUTORIAL"].toString())
            wheel = true
        }
    }

    override fun destroyState() {
        isFocusable = false
        soundManager.destroy()
        soundManagerEar.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {
        when (GestureManager.doubleTapDetect(event)) {
            GestureType.DOUBLE_TAP -> {
                MovementManager.register()
                Settings.globalSounds.playSound(Resources.acceptSound)
                LevelManager.changeLevel(GameLevel())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}