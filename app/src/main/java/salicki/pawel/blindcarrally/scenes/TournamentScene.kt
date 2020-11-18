package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.LevelTypeEnum
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SharedPreferencesManager
import salicki.pawel.blindcarrally.utils.SoundManager

class TournamentScene : SurfaceView(Settings.CONTEXT), ILevel {

    private var textsTournamentSelection: HashMap<String, String> = HashMap()
    private var textsTournament: HashMap<String, String> = HashMap()

    private var soundManager: SoundManager =
        SoundManager()
    private var swipe: Boolean = false
    private var tournamentSelectionData = arrayListOf<OptionSelectionData>()
    private var tournamentIterator: Int = 0
    private var lastOption: Int = -1

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initTournamentSelectionOptions()
    }

    private fun initTournamentSelectionOptions() {

        tournamentSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.NEW_TOURNAMENT,
                "NEW",
                "Argentyna",
                false
            )
        )
        tournamentSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.CONTINUE_TOURNAMENT,
                "CONTINUE",
                "Australia",
                false
            )
        )
        tournamentSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.RETURN,
                "RETURN",
                "Polska",
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
        textsTournament.putAll(
            OpenerCSV.readData(
                R.raw.tournament_tts,
                Settings.languageTtsEnum
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsTournament["TOURNAMENT_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(textsTournament["NEW"].toString())
    }

    override fun updateState() {
        if (tournamentSelectionData[tournamentIterator].selected && lastOption != tournamentIterator) {
            textsTournament[tournamentSelectionData[tournamentIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = tournamentIterator
        }

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            TextToSpeechManager.speakNow(textsTournamentSelection["TOURNAMENT_TUTORIAL"].toString())

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
                tournamentIterator++

                if (tournamentIterator >= tournamentSelectionData.size) {
                    tournamentIterator = 0
                }

                swipe = true
                idleTimeSeconds = 0
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                tournamentIterator--
                if (tournamentIterator < 0) {
                    tournamentIterator = tournamentSelectionData.size - 1
                }

                swipe = true
                idleTimeSeconds = 0
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                changeLevel(tournamentIterator)
                idleTimeSeconds = 0
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 3 -> {
                    tournamentIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 2 -> {
                    tournamentIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 3 -> {
                    tournamentIterator = 2
                }
            }
            idleTimeSeconds = 0
        }

        tournamentSelectionData.forEach {
            it.selected = false
        }

        tournamentSelectionData[tournamentIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (tournamentSelectionData[option].levelType) {
            LevelTypeEnum.NEW_TOURNAMENT -> {
                LevelManager.changeLevel(TournamentQuestionLevel())
            }
            LevelTypeEnum.CONTINUE_TOURNAMENT -> {
                var tmp: Boolean = false
                var isTournament = SharedPreferencesManager.loadConfiguration("isTournament")
                if (isTournament != null && isTournament != "") {
                    tmp = isTournament == "1"
                }

                if(tmp){
                    LevelManager.changeLevel(TournamentGarageScene())
                } else{
                    TextToSpeechManager.speakNow(textsTournament["CANT_CONTINUE"].toString())
                }
            }
            LevelTypeEnum.RETURN -> {
                LevelManager.changeLevel(GameModeScene())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}