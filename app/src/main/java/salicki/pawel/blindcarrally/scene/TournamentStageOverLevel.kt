package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.OptionSelectionData
import salicki.pawel.blindcarrally.data.StageResultData
import salicki.pawel.blindcarrally.data.TrackRaceData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class TournamentStageOverLevel(raceData: StageResultData) : SurfaceView(Settings.CONTEXT), ILevel {

    private var textsTournamentStageOver: HashMap<String, String> = HashMap()
    private var raceData: StageResultData = raceData
    private var tournamentStageOverSelectionData= arrayListOf<OptionSelectionData>()
    private var soundManager: SoundManager = SoundManager()
    private var swipe: Boolean = false
    private var tournamentStageOverIterator: Int = 0
    private var lastOption: Int = -1
    private var tournamentStageNumber: Int = 0
    private var finishedTournament: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        updateStage()
        isTournamentFinished()
        initTournamentStageOverSelectionOptions()
    }

    private fun isTournamentFinished() {
        finishedTournament = tournamentStageNumber >= StagesResources.stageList.size

        if(finishedTournament){
            SharedPreferencesManager.saveConfiguration("isTournament", "0")
            SharedPreferencesManager.saveConfiguration("tournamentStageNumber", "0")
        }
    }

    private fun updateStage(){
        var stageNumber = SharedPreferencesManager.loadConfiguration("tournamentStageNumber")
        if (stageNumber != null && stageNumber != "") {
            tournamentStageNumber = stageNumber.toInt()
            tournamentStageNumber++
            SharedPreferencesManager.saveConfiguration("tournamentStageNumber", tournamentStageNumber.toString())
        }
    }

    private fun initTournamentStageOverSelectionOptions() {
        tournamentStageOverSelectionData.add(
            OptionSelectionData(
                LevelType.MODE_DESCRIPTION,
                "READ",
                "Wznów",
                false
            )
        )
        if(!finishedTournament){
            tournamentStageOverSelectionData.add(
                OptionSelectionData(
                    LevelType.TOURNAMENT,
                    "GARAGE",
                    "Do menu",
                    false
                )
            )
        }
        tournamentStageOverSelectionData.add(
            OptionSelectionData(
                LevelType.MENU,
                "MENU",
                "Do menu",
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
        textsTournamentStageOver.putAll(
            OpenerCSV.readData(
                R.raw.tournament_stage_over_tts,
                Settings.languageTTS
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsTournamentStageOver["STAGE_OVER"].toString())

        if(finishedTournament){
            TextToSpeechManager.speakQueue(textsTournamentStageOver["END_TOURNAMENT"].toString())
        }
        else {
            TextToSpeechManager.speakQueue(textsTournamentStageOver["NEXT_STAGE"].toString())
        }

        scoreDescription()
        TextToSpeechManager.speakQueue(textsTournamentStageOver["READ"].toString())
    }

    private fun scoreDescription(){
        TextToSpeechManager.speakQueue(textsTournamentStageOver["CAR_DAMAGE"].toString() + raceData.carDamage + "%.")
        TextToSpeechManager.speakQueue(textsTournamentStageOver["TIME"].toString() + raceData.time + textsTournamentStageOver["SECONDS"].toString())
        TextToSpeechManager.speakQueue(textsTournamentStageOver["SCORE"].toString() + raceData.score + textsTournamentStageOver["POINTS"].toString())
    }

    override fun updateState(deltaTime: Int) {
        if (tournamentStageOverSelectionData[tournamentStageOverIterator].selected && lastOption != tournamentStageOverIterator) {
            textsTournamentStageOver[tournamentStageOverSelectionData[tournamentStageOverIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = tournamentStageOverIterator
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
                tournamentStageOverIterator++

                if (tournamentStageOverIterator >= tournamentStageOverSelectionData.size) {
                    tournamentStageOverIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                tournamentStageOverIterator--
                if (tournamentStageOverIterator < 0) {
                    tournamentStageOverIterator = tournamentStageOverSelectionData.size - 1
                }

                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(Resources.acceptSound)
                changeLevel(tournamentStageOverIterator)
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            if(finishedTournament){
                when {
                    holdPosition < Settings.SCREEN_WIDTH / 2 -> {
                        tournamentStageOverIterator = 0
                    }
                    holdPosition < Settings.SCREEN_WIDTH / 2 * 2 -> {
                        tournamentStageOverIterator = 1
                    }
                }
            } else {
                when {
                    holdPosition < Settings.SCREEN_WIDTH / 3 -> {
                        tournamentStageOverIterator = 0
                    }
                    holdPosition < Settings.SCREEN_WIDTH / 3 * 2 -> {
                        tournamentStageOverIterator = 1
                    }
                    holdPosition < Settings.SCREEN_WIDTH / 3 * 3 -> {
                        tournamentStageOverIterator = 2
                    }
                }
            }

        }

        tournamentStageOverSelectionData.forEach {
            it.selected = false
        }

        tournamentStageOverSelectionData[tournamentStageOverIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (tournamentStageOverSelectionData[option].levelType) {
            LevelType.MODE_DESCRIPTION -> {
                scoreDescription()
            }
            LevelType.TOURNAMENT -> {
                LevelManager.changeLevel(TournamentGarageLevel())
            }
            LevelType.MENU -> {
                LevelManager.changeLevel(MenuLevel())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}