package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class TournamentQuestionLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var newTournament: Boolean = true
    private var soundManager: SoundManager = SoundManager()
    private var tournamentQuestionIterator: Int = 0
    private var lastOption: Int = 0
    private var swipe: Boolean = false
    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init{
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["TOURNAMENT_QUESTION"].toString())
        TextToSpeechManager.speakQueue(texts["NO"].toString())
    }

    private fun initSoundManager(){
        soundManager.initSoundManager()

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.tournament_question_tts, Settings.languageTTS))
    }

    override fun updateState(deltaTime: Int) {
        if(tournamentQuestionIterator != lastOption){
            if (newTournament) {
                texts["YES"]?.let { TextToSpeechManager.speakNow(it) }
            } else {
                texts["NO"]?.let { TextToSpeechManager.speakNow(it) }
            }

            lastOption = tournamentQuestionIterator
        }

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            TextToSpeechManager.speakNow(texts["QUIT_TUTORIAL"].toString())

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
            GestureType.SWIPE_LEFT, GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                newTournament = !newTournament
                tournamentQuestionIterator = if (newTournament) {
                    0
                } else {
                    1
                }

                swipe = true
                idleTimeSeconds = 0
            }
        }

        when (GestureManager.doubleTapDetect(event)) {
            GestureType.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(Resources.acceptSound)
                if (!newTournament) {
                    LevelManager.changeLevel(TournamentLevel())
                } else {
                    SharedPreferencesManager.saveConfiguration("isTournament", "1")
                    SharedPreferencesManager.saveConfiguration("tournamentStageNumber", "0")
                    LevelManager.changeLevel(CarSelectionLevel())
                }
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 2 -> {
                    tournamentQuestionIterator = 0
                    newTournament = true
                }
                holdPosition < Settings.SCREEN_WIDTH / 2 * 2 -> {
                    tournamentQuestionIterator = 1
                    newTournament = false
                }
            }

            idleTimeSeconds = 0
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}