package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.OptionSelectionData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager

class CarPerformanceLevel : SurfaceView(Settings.CONTEXT), ILevel {
    private var textsPerformanceSelection: HashMap<String, String> = HashMap()

    private var soundManager: SoundManager = SoundManager()
    private var swipe: Boolean = false
    private var performanceSelectionData = arrayListOf<OptionSelectionData>()
    private var performanceIterator: Int = 0
    private var lastOption: Int = -1
    private var carNumber: Int = 1
    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    private var carDescription: String = ""

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initPerformanceSelectionOptions()
        initCarDescription()

  //      GameOptions.carDescription = carDescription
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
                "Argentyna",
                false
            )
        )
        performanceSelectionData.add(
            OptionSelectionData(
                SelectionEnum.SELECTION_2,
                "BACK",
                "Argentyna",
                false
            )
        )
        performanceSelectionData.add(
            OptionSelectionData(
                SelectionEnum.SELECTION_3,
                "ACCEPT",
                "Argentyna",
                false
            )
        )
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    private fun readTTSTextFile() {
        textsPerformanceSelection.putAll(
            OpenerCSV.readData(
                R.raw.car_performance_tts,
                Settings.languageTTS
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsPerformanceSelection["PERFORMANCE_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(textsPerformanceSelection["READ"].toString())
    }

    override fun updateState(deltaTime: Int) {
        if (performanceSelectionData[performanceIterator].selected && lastOption != performanceIterator) {
            textsPerformanceSelection[performanceSelectionData[performanceIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = performanceIterator
        }

        if (!TextToSpeechManager.isSpeaking()) {
            idleTime++

            if (idleTime % 30 == 0) {
                idleTimeSeconds++
            }
        }

        if (idleTimeSeconds > 10) {
            TextToSpeechManager.speakNow(textsPerformanceSelection["PERFORMANCE_TUTORIAL"].toString())

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
            GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                performanceIterator++

                if (performanceIterator >= performanceSelectionData.size) {
                    performanceIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                performanceIterator--
                if (performanceIterator < 0) {
                    performanceIterator = performanceSelectionData.size - 1
                }

                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(Resources.acceptSound)
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
            }
            SelectionEnum.SELECTION_2, -> {
                LevelManager.popLevel()
            }
            SelectionEnum.SELECTION_3, -> {
                if(GameOptions.gamemode == RacingModeEnum.SINGLE_RACE){
                    LevelManager.changeLevel(CalibrationLevel())
                }
                else if(GameOptions.gamemode == RacingModeEnum.TOURNAMENT_MODE){
                    SharedPreferencesManager.saveConfiguration("carTournamentNumber", carNumber.toString())
                    SharedPreferencesManager.saveConfiguration("carTournamentTopSpeed", GameOptions.carTopSpeed.toString())
                    SharedPreferencesManager.saveConfiguration("carTournamentAcceleration", GameOptions.carAcceleration.toString())
                    SharedPreferencesManager.saveConfiguration("carTournamentManeuverability", GameOptions.carManeuverability.toString())
                    LevelManager.changeLevel(TournamentGarageLevel())
                } else {
                    LevelManager.changeLevel(CalibrationLevel())
                }
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}