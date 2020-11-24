package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.*
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.GameOptions
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SharedPreferencesManager
import salicki.pawel.blindcarrally.utils.SoundManager

class CarDestroyedScene : SurfaceView(Settings.CONTEXT), ILevel {
    private var textsDestroyedSelection: HashMap<String, String> = HashMap()
    private var destroyedImage: OptionImage = OptionImage()
    private var screenTexts: HashMap<String, String> = HashMap()
    private var optionText: TextObject = TextObject()
    private var soundManager: SoundManager =
        SoundManager()
    private var swipe: Boolean = false
    private var destroyedSelectionData = arrayListOf<OptionSelectionData>()
    private var destroyedIterator: Int = 0
    private var lastOption: Int = -1

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initPerformanceSelectionOptions()

        destroyedImage.setFullScreenImage(R.drawable.try_again)
        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
    }

    private fun initPerformanceSelectionOptions() {
        destroyedSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.CALIBRATION,
                "TRY_AGAIN",
                "TRY_AGAIN",
                false
            )
        )
        destroyedSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.MENU,
                "MENU",
                "MENU",
                false
            )
        )
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(RawResources.swapSound)
        soundManager.addSound(RawResources.acceptSound)
    }

    private fun readTTSTextFile() {
        textsDestroyedSelection.putAll(
            OpenerCSV.readData(
                R.raw.car_destroyed_tts,
                Settings.languageTtsEnum
            )
        )

        screenTexts.putAll(
            OpenerCSV.readData(
                R.raw.car_destroyed_texts,
                Settings.languageTtsEnum
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsDestroyedSelection["DESTROYED"].toString())
        TextToSpeechManager.speakQueue(textsDestroyedSelection["TRY_AGAIN"].toString())
    }

    override fun updateState() {
        if (destroyedSelectionData[destroyedIterator].selected && lastOption != destroyedIterator) {
            textsDestroyedSelection[destroyedSelectionData[destroyedIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = destroyedIterator
        }

        if (!TextToSpeechManager.isSpeaking()) {
            idleTime++

            if (idleTime % 30 == 0) {
                idleTimeSeconds++
            }
        }

        if (idleTimeSeconds > 10) {
            TextToSpeechManager.speakNow(textsDestroyedSelection["DESTROYED"].toString())

            idleTimeSeconds = 0
        }
    }

    override fun destroyState() {
        isFocusable = false

        soundManager.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {

        swipe = false

        when (GestureManager.swipeDetect(event)) {
            GestureTypeEnum.SWIPE_RIGHT -> {
                soundManager.playSound(RawResources.swapSound)
                destroyedIterator++

                if (destroyedIterator >= destroyedSelectionData.size) {
                    destroyedIterator = 0
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                destroyedIterator--
                if (destroyedIterator < 0) {
                    destroyedIterator = destroyedSelectionData.size - 1
                }

                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                changeLevel(destroyedIterator)
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 2 -> {
                    destroyedIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 2 * 2 -> {
                    destroyedIterator = 1
                }
            }
        }

        destroyedSelectionData.forEach {
            it.selected = false
        }

        destroyedSelectionData[destroyedIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (destroyedSelectionData[option].levelType) {
            LevelTypeEnum.CALIBRATION -> {
                LevelManager.changeLevel(CalibrationScene())
            }
            LevelTypeEnum.MENU -> {
                LevelManager.changeLevel(MenuScene())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        destroyedImage.drawImage(canvas)
        screenTexts[destroyedSelectionData[destroyedIterator].textValue]?.let {
            optionText.drawText(
                canvas,
                it
            )
        }
    }
}