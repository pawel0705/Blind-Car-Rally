package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SharedPreferencesManager
import salicki.pawel.blindcarrally.utils.SoundManager
import java.security.cert.PKIXRevocationChecker

class TournamentQuestionScene : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var newTournament: Boolean = true
    private var tournamentQuestionImage: OptionImage = OptionImage()
    private var screenTexts: HashMap<String, String> = HashMap()
    private var optionText: TextObject = TextObject()
    private var soundManager: SoundManager =
        SoundManager()
    private var tournamentQuestionIterator: Int = 0
    private var lastOption: Int = 0
    private var swipe: Boolean = false
    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init{
        isFocusable = true

        initSoundManager()
        readTTSTextFile()

        tournamentQuestionImage.setFullScreenImage(R.drawable.yes_no)
        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["TOURNAMENT_QUESTION"].toString())
        TextToSpeechManager.speakQueue(texts["NO"].toString())
    }

    private fun initSoundManager(){
        soundManager.initSoundManager()

        soundManager.addSound(RawResources.swapSound)
        soundManager.addSound(RawResources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.tournament_question_tts, Settings.languageTtsEnum))

        screenTexts.putAll(
            OpenerCSV.readData(
                R.raw.tournament_question_texts,
                Settings.languageTtsEnum
            )
        )
    }

    override fun updateState() {
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
            TextToSpeechManager.speakNow(texts["IDLE"].toString())

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
            GestureTypeEnum.SWIPE_LEFT, GestureTypeEnum.SWIPE_RIGHT -> {
                soundManager.playSound(RawResources.swapSound)
                newTournament = !newTournament
                tournamentQuestionIterator = if (newTournament) {
                    0
                } else {
                    1
                }

                swipe = true
                idleTimeSeconds = 0
            }
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.changeLevel(TournamentScene())
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                TextToSpeechManager.speakNow(texts["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleTimeSeconds = 0
                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {
            GestureTypeEnum.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(RawResources.acceptSound)
                if (!newTournament) {
                    LevelManager.changeLevel(TournamentScene())
                } else {
                    SharedPreferencesManager.saveConfiguration("isTournament", "1")
                    SharedPreferencesManager.saveConfiguration("tournamentStageNumber", "0")
                    LevelManager.changeLevel(CarSelectionScene())
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
        tournamentQuestionImage.drawImage(canvas)

        if(newTournament){
            screenTexts["YES"]?.let { optionText.drawText(canvas, it) }
        }
        else {
            screenTexts["NO"]?.let { optionText.drawText(canvas, it) }
        }
    }
}