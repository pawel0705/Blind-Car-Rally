package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.LevelTypeEnum
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

class TournamentScene : SurfaceView(Settings.CONTEXT), ILevel {

    private var textsTournamentSelection: HashMap<String, String> = HashMap()
    private var textsTournament: HashMap<String, String> = HashMap()
    private var screenTexts: HashMap<String, String> = HashMap()
    private var optionText: TextObject = TextObject()
    private var optionTournamentDescription: TextObject = TextObject()
    private var tournamentImage: OptionImage = OptionImage()
    private var soundManager: SoundManager =
        SoundManager()
    private var swipe: Boolean = false
    private var tournamentSelectionData = arrayListOf<OptionSelectionData>()
    private var tournamentIterator: Int = 0
    private var lastOption: Int = -1

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0
    private var drawDescription: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initTournamentSelectionOptions()

        tournamentImage.setFullScreenImage(R.drawable.tournament_mode)

        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
        screenTexts["CANT_CONTINUE"]?.let {
            optionTournamentDescription.initMultiLineText(
                R.font.montserrat,
                R.dimen.informationSize,
                Settings.SCREEN_WIDTH / 2F,
                Settings.SCREEN_HEIGHT / 10F,
                it
            )
        }
    }

    private fun initTournamentSelectionOptions() {

        tournamentSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.NEW_TOURNAMENT,
                "NEW",
                "NEW",
                false
            )
        )
        tournamentSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.CONTINUE_TOURNAMENT,
                "CONTINUE",
                "CONTINUE",
                false
            )
        )
        tournamentSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.RETURN,
                "RETURN",
                "RETURN",
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

        screenTexts.putAll(
            OpenerCSV.readData(
                R.raw.tournament_texts,
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
            drawDescription = false
            lastOption = tournamentIterator
        }

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            TextToSpeechManager.speakNow(textsTournamentSelection["IDLE"].toString())

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
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.changeLevel(GameModeScene())
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                TextToSpeechManager.speakNow(textsTournamentSelection["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleTimeSeconds = 0
                swipe = true
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
                LevelManager.changeLevel(TournamentQuestionScene())
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
                    drawDescription = true
                    TextToSpeechManager.speakNow(textsTournament["CANT_CONTINUE"].toString())
                }
            }
            LevelTypeEnum.RETURN -> {
                LevelManager.changeLevel(GameModeScene())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        tournamentImage.drawImage(canvas)

        if(!drawDescription){
            screenTexts[tournamentSelectionData[tournamentIterator].textValue]?.let {
                optionText.drawText(
                    canvas,
                    it
                )
            }
        } else {
            optionTournamentDescription.drawMultilineText(canvas)
        }
    }
}