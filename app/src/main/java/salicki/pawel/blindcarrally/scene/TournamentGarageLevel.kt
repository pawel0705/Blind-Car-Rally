package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.OptionSelectionData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class TournamentGarageLevel : SurfaceView(Settings.CONTEXT), ILevel {
    private var textsTournamentGarageSelection: HashMap<String, String> = HashMap()

    private var textsNations: HashMap<String, String> = HashMap()
    private var textsStages: HashMap<String, String> = HashMap()

    private var textsCarDescriptions: HashMap<String, String> = HashMap()

    private var soundManager: SoundManager = SoundManager()
    private var swipe: Boolean = false
    private var garageSelectionData = arrayListOf<OptionSelectionData>()
    private var garageIterator: Int = 0
    private var lastOption: Int = -1

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    private var carDescription: String = ""
    private var nextStageDescription: String = ""

    private var stageIterator: Int = 0


    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initCarSelectionOptions()
        getStageNumber()
        loadCarDescription()
        loadStageDescription()
    }

    private fun getStageNumber(){
        var stage = SharedPreferencesManager.loadConfiguration("tournamentStageNumber")
        if (stage != null && stage != "") {
            stageIterator = stage.toInt()
        }
    }

    private fun initCarSelectionOptions() {
        garageSelectionData.add(
            OptionSelectionData(
                TournamentGarageEnum.CAR_INFORMATION,
                "CAR_INFORMATION",
                "Argentyna",
                false
            )
        )
        garageSelectionData.add(
            OptionSelectionData(
                TournamentGarageEnum.STAGE_INFORMATION,
                "STAGE_INFORMATION",
                "Argentyna",
                false
            )
        )
        garageSelectionData.add(
            OptionSelectionData(
                TournamentGarageEnum.START_STAGE,
                "NEXT_STAGE",
                "Argentyna",
                false
            )
        )
        garageSelectionData.add(
            OptionSelectionData(
                TournamentGarageEnum.MENU,
                "MENU",
                "Argentyna",
                false
            )
        )
    }

    private fun loadCarDescription() {

        var number = SharedPreferencesManager.loadConfiguration("carTournamentNumber")
        if (number != null && number != "") {
            carDescription = textsCarDescriptions["CAR_$number"].toString()
        }

        GameOptions.carTopSpeed =
            SharedPreferencesManager.loadConfiguration("carTournamentTopSpeed")?.toFloat() ?: 1F

        GameOptions.carAcceleration =
            SharedPreferencesManager.loadConfiguration("carTournamentAcceleration")?.toFloat() ?: 1F

        GameOptions.carManeuverability =
            SharedPreferencesManager.loadConfiguration("carTournamentManeuverability")?.toFloat() ?: 1F
    }

    private fun loadStageDescription(){
        nextStageDescription = textsTournamentGarageSelection["STAGE_DESCRIPTION"] +
                textsNations[StagesResources.stageList[stageIterator].nationKey] +
                textsStages[StagesResources.stageList[stageIterator].stageKey]
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    private fun readTTSTextFile() {
        textsTournamentGarageSelection.putAll(
            OpenerCSV.readData(
                R.raw.tournament_garage_tts,
                Settings.languageTTS
            )
        )

        textsCarDescriptions.putAll(
            OpenerCSV.readData(
                R.raw.car_performance_tts,
                Settings.languageTTS
            )
        )

        textsNations.putAll(
            OpenerCSV.readData(
                R.raw.tracks_tts,
                Settings.languageTTS
            )
        )

        textsStages.putAll(
            OpenerCSV.readData(
                R.raw.nation_roads_tts,
                Settings.languageTTS
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsTournamentGarageSelection["GARAGE_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(textsTournamentGarageSelection["CAR_INFORMATION"].toString())
    }

    override fun updateState(deltaTime: Int) {
        if (garageSelectionData[garageIterator].selected && lastOption != garageIterator) {
            textsTournamentGarageSelection[garageSelectionData[garageIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = garageIterator
        }

        if (!TextToSpeechManager.isSpeaking()) {
            idleTime++

            if (idleTime % 30 == 0) {
                idleTimeSeconds++
            }
        }

        if (idleTimeSeconds > 10) {
            TextToSpeechManager.speakNow(textsTournamentGarageSelection["GARAGE_TUTORIAL"].toString())

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
                garageIterator++

                if (garageIterator >= garageSelectionData.size) {
                    garageIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                garageIterator--
                if (garageIterator < 0) {
                    garageIterator = garageSelectionData.size - 1
                }

                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(Resources.acceptSound)
                changeLevel(garageIterator)
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 4 -> {
                    garageIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 4 * 2 -> {
                    garageIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 4 * 3 -> {
                    garageIterator = 2
                }
                holdPosition < Settings.SCREEN_WIDTH / 4 * 4 -> {
                    garageIterator = 3
                }
            }
        }

        garageSelectionData.forEach {
            it.selected = false
        }

        garageSelectionData[garageIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (garageSelectionData[option].levelType) {
            TournamentGarageEnum.CAR_INFORMATION -> {
                TextToSpeechManager.speakNow(carDescription)
            }
            TournamentGarageEnum.STAGE_INFORMATION -> {
                TextToSpeechManager.speakNow(nextStageDescription)
            }
            TournamentGarageEnum.START_STAGE -> {
                GameOptions.stage = StagesResources.stageList[stageIterator].stageEnum
                GameOptions.nation = StagesResources.stageList[stageIterator].nation
                GameOptions.gamemode = RacingModeEnum.TOURNAMENT_MODE
                LevelManager.changeLevel(CalibrationLevel())
            }
            TournamentGarageEnum.MENU -> {
                LevelManager.stackLevel(ReturnMenuLevel())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}