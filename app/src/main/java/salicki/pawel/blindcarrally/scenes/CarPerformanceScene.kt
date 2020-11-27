package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.CarEnum
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.RacingModeEnum
import salicki.pawel.blindcarrally.enums.SelectionEnum
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

class CarPerformanceScene : SurfaceView(Settings.CONTEXT), ILevel {
    private var textsPerformanceSelection: HashMap<String, String> = HashMap()
    private var screenTexts: HashMap<String, String> = HashMap()
    private var optionText: TextObject = TextObject()
    private var optionCarDescription: TextObject = TextObject()
    private var soundManager: SoundManager =
        SoundManager()
    private var swipe: Boolean = false
    private var selectionImage: OptionImage = OptionImage()
    private var performanceSelectionData = arrayListOf<OptionSelectionData>()
    private var performanceIterator: Int = 0
    private var lastOption: Int = -1
    private var carNumber: Int = 1
    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0
    private var drawDescription: Boolean = false
    private var carDescription: String = ""

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initPerformanceSelectionOptions()
        initCarDescription()
        selectionImage.setFullScreenImage(R.drawable.car_performance)
        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
        screenTexts["CAR_" + (GameOptions.car.ordinal + 1)]?.let {
            optionCarDescription.initMultiLineText(
                R.font.montserrat,
                R.dimen.informationSize,
                Settings.SCREEN_WIDTH / 2F,
                Settings.SCREEN_HEIGHT / 10F,
                it
            )
        }
    }

    private fun initCarDescription() {
        when (GameOptions.car) {
            CarEnum.CAR_1 -> {
                GameOptions.carTopSpeed = 0.1F
                GameOptions.carAcceleration = 0.2F
                GameOptions.carManeuverability = 0.3F
                carNumber = 1
                carDescription = textsPerformanceSelection["CAR_1"].toString()
            }
            CarEnum.CAR_2 -> {
                GameOptions.carTopSpeed = 0.2F
                GameOptions.carAcceleration = 0.3F
                GameOptions.carManeuverability = 0.5F
                carNumber = 2
                carDescription = textsPerformanceSelection["CAR_2"].toString()
            }
            CarEnum.CAR_3 -> {
                GameOptions.carTopSpeed = 0.5F
                GameOptions.carAcceleration = 0.4F
                GameOptions.carManeuverability = 0.5F
                carNumber = 3
                carDescription = textsPerformanceSelection["CAR_3"].toString()
            }
            CarEnum.CAR_4 -> {
                GameOptions.carTopSpeed = 0.9F
                GameOptions.carAcceleration = 0.6F
                GameOptions.carManeuverability = 0.7F
                carNumber = 4
                carDescription = textsPerformanceSelection["CAR_4"].toString()
            }
            CarEnum.CAR_5 -> {
                GameOptions.carTopSpeed = 1F
                GameOptions.carAcceleration = 1F
                GameOptions.carManeuverability = 1F
                carNumber = 5
                carDescription = textsPerformanceSelection["CAR_5"].toString()
            }
        }
    }

    private fun initPerformanceSelectionOptions() {
        performanceSelectionData.add(
            OptionSelectionData(
                SelectionEnum.SELECTION_1,
                "READ",
                "READ",
                false
            )
        )
        performanceSelectionData.add(
            OptionSelectionData(
                SelectionEnum.SELECTION_2,
                "BACK",
                "BACK",
                false
            )
        )
        performanceSelectionData.add(
            OptionSelectionData(
                SelectionEnum.SELECTION_3,
                "ACCEPT",
                "ACCEPT",
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
        textsPerformanceSelection.putAll(
            OpenerCSV.readData(
                R.raw.car_performance_tts,
                Settings.languageTtsEnum
            )
        )

        screenTexts.putAll(
            OpenerCSV.readData(
                R.raw.car_performance_texts,
                Settings.languageTtsEnum
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsPerformanceSelection["PERFORMANCE_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(textsPerformanceSelection["READ"].toString())
    }

    override fun updateState() {
        if (performanceSelectionData[performanceIterator].selected && lastOption != performanceIterator) {
            textsPerformanceSelection[performanceSelectionData[performanceIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = performanceIterator
            drawDescription = false
        }

        if (!TextToSpeechManager.isSpeaking()) {
            idleTime++

            if (idleTime % 30 == 0) {
                idleTimeSeconds++
            }
        }

        if (idleTimeSeconds > 10) {
            TextToSpeechManager.speakNow(textsPerformanceSelection["IDLE"].toString())

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
                performanceIterator++

                if (performanceIterator >= performanceSelectionData.size) {
                    performanceIterator = 0
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                performanceIterator--
                if (performanceIterator < 0) {
                    performanceIterator = performanceSelectionData.size - 1
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.changeLevel(CarSelectionScene())
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                TextToSpeechManager.speakNow(textsPerformanceSelection["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleTimeSeconds = 0
                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                changeLevel(performanceIterator)
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 3 -> {
                    performanceIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 2 -> {
                    performanceIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 3 -> {
                    performanceIterator = 2
                }

            }
        }

        performanceSelectionData.forEach {
            it.selected = false
        }

        performanceSelectionData[performanceIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (performanceSelectionData[option].levelType) {
            SelectionEnum.SELECTION_1, -> {
                TextToSpeechManager.speakNow(carDescription)
                drawDescription = true
            }
            SelectionEnum.SELECTION_2, -> {
                LevelManager.changeLevel(CarSelectionScene())
            }
            SelectionEnum.SELECTION_3, -> {
                if(GameOptions.gamemode == RacingModeEnum.SINGLE_RACE){
                    LevelManager.changeLevel(CalibrationScene())
                }
                else if(GameOptions.gamemode == RacingModeEnum.TOURNAMENT_MODE){
                    SharedPreferencesManager.saveConfiguration("carTournamentNumber", carNumber.toString())
                    SharedPreferencesManager.saveConfiguration("carTournamentTopSpeed", GameOptions.carTopSpeed.toString())
                    SharedPreferencesManager.saveConfiguration("carTournamentAcceleration", GameOptions.carAcceleration.toString())
                    SharedPreferencesManager.saveConfiguration("carTournamentManeuverability", GameOptions.carManeuverability.toString())
                    LevelManager.changeLevel(TournamentGarageScene())
                } else {
                    LevelManager.changeLevel(CalibrationScene())
                }
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        selectionImage.drawImage(canvas)

        if(!drawDescription){
            screenTexts[performanceSelectionData[performanceIterator].textValue]?.let {
                optionText.drawText(
                    canvas,
                    it
                )
            }
        } else {
            optionCarDescription.drawMultilineText(canvas)
        }
    }
}