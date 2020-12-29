package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.NationEnum
import salicki.pawel.blindcarrally.enums.StageEnum
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.GameOptions
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.DrawableResources
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.IdleSpeakManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager
import java.security.cert.PKIXRevocationChecker

class StageSelectionScene(nation: NationEnum) : SurfaceView(Settings.CONTEXT), ILevel {
    private var textsStageSelection: HashMap<String, String> = HashMap()
    private var textsNations: HashMap<String, String> = HashMap()
    private var screenTexts: HashMap<String, String> = HashMap()
    private var stageSelectionData = arrayListOf<OptionSelectionData>()

    private val stageName = nation.toString()
    private var stageImage: OptionImage = OptionImage()
    private var idleSpeak: IdleSpeakManager = IdleSpeakManager()
    private var optionText: TextObject = TextObject()
    private var soundManager: SoundManager =
        SoundManager()

    private var stageIterator: Int = 0
    private var lastOption: Int = -1

    private var swipe: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initStageSelectionOptions()

        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)

        stageImage.setFullScreenImage(DrawableResources.selectTrackView)
    }

    private fun initStageSelectionOptions() {

        stageSelectionData.add(
            OptionSelectionData(
                StageEnum.STAGE_1,
                stageName + "_1",
                stageName + "_1",
                false
            )
        )
        stageSelectionData.add(
            OptionSelectionData(
                StageEnum.STAGE_2,
                stageName + "_2",
                stageName + "_2",
                false
            )
        )
        stageSelectionData.add(
            OptionSelectionData(
                StageEnum.STAGE_3,
                stageName + "_3",
                stageName + "_3",
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
        textsStageSelection.putAll(
            OpenerCSV.readData(
             RawResources.stageSelection_TTS,
                Settings.languageTtsEnum
            )
        )
        textsNations.putAll(OpenerCSV.readData(RawResources.nationRoads_TTS, Settings.languageTtsEnum))

        screenTexts.putAll(OpenerCSV.readData(RawResources.nationRoads_TXT, Settings.languageTtsEnum))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsStageSelection["STAGE_SELECTION_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(textsNations[stageName + "_1"].toString())

        idleSpeak.initIdleString(textsStageSelection["STAGE_SELECTION_TUTORIAL"].toString())
    }

    override fun updateState() {
        if (stageSelectionData[stageIterator].selected && lastOption != stageIterator) {
            textsNations[stageSelectionData[stageIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = stageIterator
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
                stageIterator++

                if (stageIterator >= stageSelectionData.size) {
                    stageIterator = 0
                }

                swipe = true
                idleSpeak.resetIdleTimeSeconds()
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                stageIterator--
                if (stageIterator < 0) {
                    stageIterator = stageSelectionData.size - 1
                }

                swipe = true
                idleSpeak.resetIdleTimeSeconds()
            }
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.changeLevel(TrackSelectionScene())
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                TextToSpeechManager.speakNow(textsStageSelection["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleSpeak.resetIdleTimeSeconds()
                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                changeLevel(stageIterator)
                idleSpeak.resetIdleTimeSeconds()
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 3 -> {
                    stageIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 2 -> {
                    stageIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 3 -> {
                    stageIterator = 2
                }
            }
            idleSpeak.resetIdleTimeSeconds()
        }

        stageSelectionData.forEach {
            it.selected = false
        }

        stageSelectionData[stageIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        GameOptions.stage = stageSelectionData[option].levelType as StageEnum
        LevelManager.changeLevel(CarSelectionScene())
    }

    override fun redrawState(canvas: Canvas) {
        stageImage.drawImage(canvas)

        screenTexts[ stageSelectionData[stageIterator].textValue]?.let {
            optionText.drawText(canvas,
                it
            )
        }
    }
}