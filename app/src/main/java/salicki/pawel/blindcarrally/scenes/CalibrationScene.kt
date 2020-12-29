package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.gameresources.MovementManager
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.timerTask

class CalibrationScene : SurfaceView(Settings.CONTEXT), ILevel {
    private var textsCalibration: HashMap<String, String> = HashMap()
    private var soundManager: SoundManager =
        SoundManager()
    private var soundManagerEar: SoundManager =
        SoundManager()
    private var text: TextObject = TextObject()

    private var time = 0
    private var timeSeconds = 0

    private var leftEar: Boolean = false
    private var rightEar: Boolean = false
    private var wheel: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()

        text.initMultiLineText(
            R.font.montserrat, R.dimen.informationSize,
            Settings.SCREEN_WIDTH / 2F,
            Settings.SCREEN_HEIGHT / 8F,
            textsCalibration["CALIBRATION_TUTORIAL"].toString()
        )
    }

    private fun initSoundManager() {
        soundManager.initSoundManager(1)
        soundManagerEar.initSoundManager(1)

        soundManager.addSound(RawResources.acceptSound)
        soundManagerEar.addSound(RawResources.beepSound)
    }

    private fun readTTSTextFile() {
        textsCalibration.putAll(
            OpenerCSV.readData(
                RawResources.calibration_TTS,
                Settings.languageTtsEnum
            )
        )
    }

    override fun initState() {
        MovementManager.newGame()
        MovementManager.pause()
    }

    override fun updateState() {

        if (!TextToSpeechManager.isSpeaking() && !leftEar) {
            TextToSpeechManager.speakNow(textsCalibration["CALIBRATION_LEFT"].toString())
            Timer().schedule(timerTask {
                soundManagerEar.playSound(RawResources.beepSound, 0.6F, 0F, 3)
            }, 1000)
            leftEar = true
        } else if (!TextToSpeechManager.isSpeaking() && !rightEar) {
            TextToSpeechManager.speakNow(textsCalibration["CALIBRATION_RIGHT"].toString())
            Timer().schedule(timerTask {
                soundManagerEar.playSound(RawResources.beepSound, 0F, 0.6F, 3)
            }, 1000)
            rightEar = true
        } else if (!TextToSpeechManager.isSpeaking() && leftEar && rightEar && !wheel) {
            TextToSpeechManager.speakNow(textsCalibration["CALIBRATION_TUTORIAL"].toString())
            wheel = true
        }

        if (!TextToSpeechManager.isSpeaking()) {
            time++

            if (time % Settings.FPS == 0) {
                timeSeconds++
            }

            if (timeSeconds >= Settings.FPS / 3) {
                rightEar = false
                leftEar = false
                wheel = false
            }
        }

    }

    override fun destroyState() {
        isFocusable = false
        soundManager.destroy()
        soundManagerEar.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {
        when (GestureManager.swipeDetect(event)) {
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.stackLevel(PauseScene())
                Settings.globalSounds.playSound(RawResources.swapSound)
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                Settings.globalSounds.playSound(RawResources.swapSound)
                rightEar = false
                leftEar = false
                wheel = false
                timeSeconds = 0
            }
        }

        when (GestureManager.doubleTapDetect(event)) {
            GestureTypeEnum.DOUBLE_TAP -> {
                MovementManager.register()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                LevelManager.changeLevel(GameScene())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        text.drawMultilineText(canvas)
    }
}