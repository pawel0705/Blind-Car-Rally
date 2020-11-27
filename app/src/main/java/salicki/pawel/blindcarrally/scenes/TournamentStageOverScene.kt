package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.datas.StageResultData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.LevelTypeEnum
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.resources.StagesResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SharedPreferencesManager
import salicki.pawel.blindcarrally.utils.SoundManager

class TournamentStageOverScene(raceData: StageResultData) : SurfaceView(Settings.CONTEXT), ILevel {

    private var textsTournamentStageOver: HashMap<String, String> = HashMap()
    private var raceData: StageResultData = raceData
    private var tournamentStageOverSelectionData= arrayListOf<OptionSelectionData>()
    private var soundManager: SoundManager =
        SoundManager()
    private var tournamentStageOverImage: OptionImage = OptionImage()
    private var swipe: Boolean = false
    private var tournamentStageOverIterator: Int = 0
    private var lastOption: Int = -1
    private var tournamentStageNumber: Int = 0
    private var finishedTournament: Boolean = false

    private var descriptionText: TextObject = TextObject()
    private var screenTexts: HashMap<String, String> = HashMap()
    private var optionText: TextObject = TextObject()
    private var drawDescription: Boolean = false

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        updateStage()
        isTournamentFinished()
        initTournamentStageOverSelectionOptions()
        tournamentStageOverImage.setFullScreenImage(R.drawable.tournament_stage_over)

        screenTexts.putAll(OpenerCSV.readData(R.raw.tournament_stage_over_text, Settings.languageTtsEnum))
        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)

        descriptionText.initMultiLineText(
            R.font.montserrat,
            R.dimen.informationSize,
            Settings.SCREEN_WIDTH / 2F,
            Settings.SCREEN_HEIGHT / 10F,
            textsTournamentStageOver["CAR_DAMAGE"].toString() + raceData.carDamage + "%." +
                textsTournamentStageOver["TIME"].toString() + raceData.time + textsTournamentStageOver["SECONDS"].toString() +
                textsTournamentStageOver["SCORE"].toString() + raceData.score + textsTournamentStageOver["POINTS"].toString()
        )
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
                LevelTypeEnum.MODE_DESCRIPTION,
                "READ",
                "READ",
                false
            )
        )
        if(!finishedTournament){
            tournamentStageOverSelectionData.add(
                OptionSelectionData(
                    LevelTypeEnum.TOURNAMENT,
                    "GARAGE",
                    "GARAGE",
                    false
                )
            )
        }
        tournamentStageOverSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.MENU,
                "MENU",
                "MENU",
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
        textsTournamentStageOver.putAll(
            OpenerCSV.readData(
                R.raw.tournament_stage_over_tts,
                Settings.languageTtsEnum
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

    override fun updateState() {
        if (tournamentStageOverSelectionData[tournamentStageOverIterator].selected && lastOption != tournamentStageOverIterator) {
            textsTournamentStageOver[tournamentStageOverSelectionData[tournamentStageOverIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            drawDescription = false
            lastOption = tournamentStageOverIterator
        }

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            TextToSpeechManager.speakNow(textsTournamentStageOver["IDLE"].toString())

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
                tournamentStageOverIterator++

                if (tournamentStageOverIterator >= tournamentStageOverSelectionData.size) {
                    tournamentStageOverIterator = 0
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                tournamentStageOverIterator--
                if (tournamentStageOverIterator < 0) {
                    tournamentStageOverIterator = tournamentStageOverSelectionData.size - 1
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.changeLevel(MenuScene())
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                TextToSpeechManager.speakNow(textsTournamentStageOver["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleTimeSeconds = 0
                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
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
            LevelTypeEnum.MODE_DESCRIPTION -> {
                drawDescription = true
                scoreDescription()
            }
            LevelTypeEnum.TOURNAMENT -> {
                LevelManager.changeLevel(TournamentGarageScene())
            }
            LevelTypeEnum.MENU -> {
                LevelManager.changeLevel(MenuScene())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        tournamentStageOverImage.drawImage(canvas)

        if(!drawDescription){
            screenTexts[tournamentStageOverSelectionData[tournamentStageOverIterator].textValue]?.let {
                optionText.drawText(
                    canvas,
                    it
                )
            }
        } else {
            descriptionText.drawMultilineText(canvas)
        }
    }
}