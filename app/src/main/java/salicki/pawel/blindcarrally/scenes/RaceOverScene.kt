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
import salicki.pawel.blindcarrally.resources.DrawableResources
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.IdleSpeakManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager

class RaceOverScene(raceData: StageResultData) : SurfaceView(Settings.CONTEXT), ILevel {
    private var screenTexts: HashMap<String, String> = HashMap()
    private var textsRaceOver: HashMap<String, String> = HashMap()
    private var raceOverSelectionData= arrayListOf<OptionSelectionData>()

    private var raceData: StageResultData = raceData

    private var raceOverImage: OptionImage = OptionImage()
    private var soundManager: SoundManager =
        SoundManager()
    private var descriptionText: TextObject = TextObject()
    private var optionText: TextObject = TextObject()
    private var idleSpeak: IdleSpeakManager = IdleSpeakManager()

    private var raceOverIterator: Int = 0
    private var lastOption: Int = -1

    private var drawDescription: Boolean = false
    private var swipe: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initTrackSelectionOptions()
        raceOverImage.setFullScreenImage(DrawableResources.raceOverView)

        screenTexts.putAll(OpenerCSV.readData(RawResources.raceMode_TXT, Settings.languageTtsEnum))
        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)

            descriptionText.initMultiLineText(
                R.font.montserrat,
                R.dimen.informationSize,
                Settings.SCREEN_WIDTH / 2F,
                Settings.SCREEN_HEIGHT / 10F,
                textsRaceOver["CAR_DAMAGE"].toString() + " " + raceData.carDamage + "%. " +
                     textsRaceOver["TIME"].toString() + " " + raceData.time + textsRaceOver["SECONDS"].toString()
            )
    }

    private fun initTrackSelectionOptions() {
        raceOverSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.MENU,
                "MENU",
                "MENU",
                false
            )
        )
        raceOverSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.GAME,
                "RACE_AGAIN",
                "RACE_AGAIN",
                false
            )
        )
        raceOverSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.MODE_DESCRIPTION,
                "READ_SCORE",
                "READ_SCORE",
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
        textsRaceOver.putAll(
            OpenerCSV.readData(
              RawResources.raceOver_TTS,
                Settings.languageTtsEnum
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsRaceOver["STAGE_COMPLETED_1"].toString())
        scoreDescription()
        TextToSpeechManager.speakQueue(textsRaceOver["MENU"].toString())

        idleSpeak.initIdleString(textsRaceOver["IDLE"].toString())
    }

    private fun scoreDescription(){
        TextToSpeechManager.speakQueue(textsRaceOver["CAR_DAMAGE"].toString() + raceData.carDamage + "%.")
        TextToSpeechManager.speakQueue(textsRaceOver["TIME"].toString() + raceData.time + textsRaceOver["SECONDS"].toString())
    }

    override fun updateState() {
        if (raceOverSelectionData[raceOverIterator].selected && lastOption != raceOverIterator) {
            textsRaceOver[raceOverSelectionData[raceOverIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            drawDescription = false
            lastOption = raceOverIterator
        }

        idleSpeak.updateIdleStatus()
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
                raceOverIterator++

                if (raceOverIterator >= raceOverSelectionData.size) {
                    raceOverIterator = 0
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                raceOverIterator--
                if (raceOverIterator < 0) {
                    raceOverIterator = raceOverSelectionData.size - 1
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.changeLevel(MenuScene())
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                TextToSpeechManager.speakNow(textsRaceOver["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleSpeak.resetIdleTimeSeconds()
                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                changeLevel(raceOverIterator)
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 3 -> {
                    raceOverIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 2 -> {
                    raceOverIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 3 -> {
                    raceOverIterator = 2
                }
            }
        }

        raceOverSelectionData.forEach {
            it.selected = false
        }

        raceOverSelectionData[raceOverIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (raceOverSelectionData[option].levelType) {
            LevelTypeEnum.MENU -> {
                LevelManager.changeLevel(MenuScene())
            }
            LevelTypeEnum.GAME -> {
                LevelManager.changeLevel(GameScene())
            }
            LevelTypeEnum.MODE_DESCRIPTION -> {
                scoreDescription()
                drawDescription = true
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        raceOverImage.drawImage(canvas)

        if(!drawDescription){
            screenTexts[raceOverSelectionData[raceOverIterator].textValue]?.let {
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