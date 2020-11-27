package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.CarEnum
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
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
import salicki.pawel.blindcarrally.utils.SoundManager

class CarSelectionScene() : SurfaceView(Settings.CONTEXT), ILevel {
    private var textsCarsSelection: HashMap<String, String> = HashMap()
    private var selectionImage: OptionImage = OptionImage()
    private var screenTexts: HashMap<String, String> = HashMap()
    private var optionText: TextObject = TextObject()
    private var soundManager: SoundManager =
        SoundManager()
    private var swipe: Boolean = false
    private var carsSelectionData = arrayListOf<OptionSelectionData>()
    private var carIterator: Int = 0
    private var lastOption: Int = -1

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initCarSelectionOptions()

        selectionImage.setFullScreenImage(R.drawable.select_car)
        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
    }

    private fun initCarSelectionOptions() {
        carsSelectionData.add(
            OptionSelectionData(
                CarEnum.CAR_1,
                "CAR_1",
                "CAR_1",
                false
            )
        )
        carsSelectionData.add(
            OptionSelectionData(
                CarEnum.CAR_2,
                "CAR_2",
                "CAR_2",
                false
            )
        )
        carsSelectionData.add(
            OptionSelectionData(
                CarEnum.CAR_3,
                "CAR_3",
                "CAR_3",
                false
            )
        )
        carsSelectionData.add(
            OptionSelectionData(
                CarEnum.CAR_4,
                "CAR_4",
                "CAR_4",
                false
            )
        )
        carsSelectionData.add(
            OptionSelectionData(
                CarEnum.CAR_5,
                "CAR_5",
                "CAR_5",
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
        textsCarsSelection.putAll(
            OpenerCSV.readData(
                R.raw.car_selection_tts,
                Settings.languageTtsEnum
            )
        )
        screenTexts.putAll(OpenerCSV.readData(R.raw.car_selection_texts, Settings.languageTtsEnum))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsCarsSelection["CAR_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(textsCarsSelection["CAR_1"].toString())
    }

    override fun updateState() {
        if (carsSelectionData[carIterator].selected && lastOption != carIterator) {
            textsCarsSelection[carsSelectionData[carIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = carIterator
        }

        if (!TextToSpeechManager.isSpeaking()) {
            idleTime++

            if (idleTime % 30 == 0) {
                idleTimeSeconds++
            }
        }

        if (idleTimeSeconds > 10) {
            TextToSpeechManager.speakNow(textsCarsSelection["IDLE"].toString())

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
                carIterator++

                if (carIterator >= carsSelectionData.size) {
                    carIterator = 0
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                carIterator--
                if (carIterator < 0) {
                    carIterator = carsSelectionData.size - 1
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.changeLevel(StageSelectionScene(GameOptions.nation))
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                TextToSpeechManager.speakNow(textsCarsSelection["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleTimeSeconds = 0
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                changeLevel(carIterator)
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 5 -> {
                    carIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 2 -> {
                    carIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 3 -> {
                    carIterator = 2
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 4 -> {
                    carIterator = 3
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 5 -> {
                    carIterator = 4
                }
            }
        }

        carsSelectionData.forEach {
            it.selected = false
        }

        carsSelectionData[carIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        GameOptions.car = carsSelectionData[option].levelType as CarEnum
        LevelManager.changeLevel(CarPerformanceScene())
    }

    override fun redrawState(canvas: Canvas) {
        selectionImage.drawImage(canvas)
        textsCarsSelection[carsSelectionData[carIterator].textValue]?.let {
            optionText.drawText(canvas,
                it
            )
        }
    }
}