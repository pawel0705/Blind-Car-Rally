package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.OptionSelectionData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType
import java.util.logging.Level

class TournamentLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var textsTournamentSelection: HashMap<String, String> = HashMap()
    private var textsTournament: HashMap<String, String> = HashMap()

    private var soundManager: SoundManager = SoundManager()
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
                LevelType.NEW_TOURNAMENT,
                "NEW",
                "Argentyna",
                false
            )
        )
        tournamentSelectionData.add(
            OptionSelectionData(
                LevelType.CONTINUE_TOURNAMENT,
                "CONTINUE",
                "Australia",
                false
            )
        )
        tournamentSelectionData.add(
            OptionSelectionData(
                LevelType.RETURN,
                "RETURN",
                "Polska",
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
        textsTournament.putAll(
            OpenerCSV.readData(
                R.raw.tournament_tts,
                Settings.languageTTS
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsTournament["TOURNAMENT_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(textsTournament["NEW"].toString())
    }

    override fun updateState(deltaTime: Int) {
        if (tournamentSelectionData[tournamentIterator].selected && lastOption != tournamentIterator) {
            tournamentSelectionData[tournamentIterator].textKey?.let {
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
            GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                tournamentIterator++

                if (tournamentIterator >= tournamentSelectionData.size) {
                    tournamentIterator = 0
                }

                swipe = true
                idleTimeSeconds = 0
            }
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                tournamentIterator--
                if (tournamentIterator < 0) {
                    tournamentIterator = tournamentSelectionData.size - 1
                }

                swipe = true
                idleTimeSeconds = 0
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(Resources.acceptSound)
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
            LevelType.NEW_TOURNAMENT -> {
                LevelManager.changeLevel(TournamentQuestionLevel())
            }
            LevelType.CONTINUE_TOURNAMENT -> {

            }
            LevelType.RETURN -> {
                LevelManager.changeLevel(GameModeLevel())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}